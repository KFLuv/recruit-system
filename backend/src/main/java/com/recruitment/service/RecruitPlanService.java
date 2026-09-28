package com.recruitment.service;

import com.recruitment.dto.RecruitPlanDTO;
import com.recruitment.dto.RecruitPositionDTO;
import com.recruitment.entity.AnnualBudgetConfig;
import com.recruitment.entity.PlanVersion;
import com.recruitment.entity.ProcessApproval;
import com.recruitment.entity.RecruitPlan;
import com.recruitment.entity.RecruitPosition;
import com.recruitment.repository.AnnualBudgetConfigRepository;
import com.recruitment.repository.PlanVersionRepository;
import com.recruitment.repository.RecruitPlanRepository;
import com.recruitment.repository.RecruitPositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecruitPlanService {

    @Autowired
    private RecruitPlanRepository recruitPlanRepository;

    @Autowired
    private RecruitPositionRepository recruitPositionRepository;

    @Autowired
    private ProcessApprovalService processApprovalService;

    @Autowired
    private AnnualBudgetConfigRepository budgetConfigRepository;

    @Autowired
    private PlanVersionRepository planVersionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initDatabase() {
        try {
            jdbcTemplate.execute("ALTER TABLE recruit_plan MODIFY COLUMN org_unit_id BIGINT DEFAULT NULL");
        } catch (Exception e) {
            // 忽略，列可能已可为空
        }
    }

    public List<RecruitPlan> findAllEntities() {
        return recruitPlanRepository.findAll();
    }

    public RecruitPlanDTO createPlan(RecruitPlan plan, Long creatorId, String creatorName) {
        // 如果没有设置状态，默认为草稿
        if (plan.getStatus() == null) {
            plan.setStatus("draft");
        }
        // 自动生成计划编号（如 PL20260001）
        if (plan.getPlanNo() == null || plan.getPlanNo().trim().isEmpty()) {
            plan.setPlanNo(generatePlanNo(plan.getPlanYear()));
        }
        plan.setCreatorId(creatorId);
        
        // 先保存计划，获取 ID
        RecruitPlan saved = recruitPlanRepository.save(plan);
        
        Long planId = saved.getId();
        
        // 计算总岗位数和总招聘人数
        List<RecruitPosition> positions = plan.getPositions();
        if (positions != null && !positions.isEmpty()) {
            int totalPositions = positions.size();
            int totalCandidates = positions.stream()
                    .mapToInt(RecruitPosition::getRecruitmentCount)
                    .sum();
            
            saved.setTotalPositions(totalPositions);
            saved.setTotalCandidates(totalCandidates);
            
            // 设置外键并保存岗位
            for (RecruitPosition position : positions) {
                position.setPlanId(planId);
            }
            recruitPositionRepository.saveAll(positions);
            
            saved = recruitPlanRepository.save(saved);
        }
        
        // 创建初始版本记录（v1）
        createVersionRecord(saved, null, creatorId, creatorName, "初始创建");
        
        return convertToDTO(saved);
    }

    /**
     * 生成计划编号：PL + 年份 + 4位序号（如 PL20260001）
     */
    private String generatePlanNo(Integer planYear) {
        int year = planYear != null ? planYear : LocalDate.now().getYear();
        String prefix = "PL" + year;
        int maxSeq = 0;
        try {
            List<String> nos = jdbcTemplate.queryForList(
                    "SELECT plan_no FROM recruit_plan WHERE plan_no LIKE ?", String.class, prefix + "%");
            for (String no : nos) {
                if (no != null && no.length() > prefix.length()) {
                    try {
                        int seq = Integer.parseInt(no.substring(prefix.length()));
                        if (seq > maxSeq) {
                            maxSeq = seq;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            // 查询失败时从随机号段开始，不影响创建
        }
        return prefix + String.format("%04d", maxSeq + 1);
    }

    public List<RecruitPlanDTO> getAllPlans(Integer year, Long orgUnitId, String status, String keyword) {
        return recruitPlanRepository.findAll().stream()
                .filter(plan -> {
                    // 已删除的计划只在显式查询 status=deleted 时返回
                    if ("deleted".equals(status)) return "deleted".equals(plan.getStatus());
                    return !"deleted".equals(plan.getStatus());
                })
                .filter(plan -> (year == null || (plan.getPlanYear() != null && year.equals(plan.getPlanYear()))))
                .filter(plan -> (orgUnitId == null || (plan.getOrgUnitId() != null && orgUnitId.equals(plan.getOrgUnitId()))))
                .filter(plan -> (status == null || plan.getStatus() == null ? status == null : plan.getStatus().equals(status)))
                .filter(plan -> (keyword == null || 
                        plan.getPlanName().contains(keyword) ||
                        (plan.getOrgUnitName() != null && plan.getOrgUnitName().contains(keyword)) ||
                        (plan.getPlanOwner() != null && plan.getPlanOwner().contains(keyword))))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void batchSubmit(List<Long> planIds) {
        for (Long id : planIds) {
            RecruitPlan plan = recruitPlanRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("招聘计划不存在：" + id));
            
            if ("draft".equals(plan.getStatus())) {
                // 批量提交时做简化校验
                validateBudgetBeforeSubmit(plan);
                plan.setStatus("submitting");
                plan.setSubmitTime(LocalDateTime.now());
                recruitPlanRepository.save(plan);
            }
        }
    }
    
    public void batchRevoke(List<Long> planIds) {
        for (Long id : planIds) {
            RecruitPlan plan = recruitPlanRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("招聘计划不存在：" + id));
            
            if ("draft".equals(plan.getStatus())) {
                plan.setStatus("revoked");
                recruitPlanRepository.save(plan);
            }
        }
    }
    public void submitPlan(Long id, Long userId, String userName) {
        RecruitPlan plan = recruitPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));
        
        // ===== 企业级校验：提交时必须检查编制预算 =====
        validateBudgetBeforeSubmit(plan);
        
        plan.setStatus("submitting");
        plan.setSubmitTime(LocalDateTime.now());
        
        // 创建多级审批流程（二级单位 → 集团HR → 集团领导）
        processApprovalService.createMultiLevelApproval("recruit_plan", id, userId, plan.getOrgUnitId());
        
        recruitPlanRepository.save(plan);
    }

    /**
     * 编制预算校验 —— 企业级管控核心方法
     * 提交审批前检查：该单位本年度已审批人数 + 本次人数 ≤ 编制上限
     */
    private void validateBudgetBeforeSubmit(RecruitPlan plan) {
        // 与 jar 原版一致：年度或单位信息缺失时跳过预算校验，不阻断提交
        if (plan.getPlanYear() == null || plan.getOrgUnitId() == null) {
            return;
        }
        
        // 查询该单位当年的编制预算配置
        Optional<AnnualBudgetConfig> configOpt = budgetConfigRepository
                .findByConfigYearAndOrgUnitId(plan.getPlanYear(), plan.getOrgUnitId());
        
        // 未配置预算时同样跳过校验（原版行为）
        if (configOpt.isEmpty()) {
            return;
        }
        
        AnnualBudgetConfig config = configOpt.get();
        
        // 计算该单位本年度 已审批通过 + 审批中 的需求人数
        Integer alreadyUsed = recruitPlanRepository.sumTotalCandidatesByOrgUnitAndYearAndStatus(
                plan.getOrgUnitId(), plan.getPlanYear(), "approved");
        Integer submitting = recruitPlanRepository.sumTotalCandidatesByOrgUnitAndYearAndStatus(
                plan.getOrgUnitId(), plan.getPlanYear(), "submitting");
        
        int usedCount = (alreadyUsed != null ? alreadyUsed : 0) + (submitting != null ? submitting : 0);
        int planCount = plan.getTotalCandidates() != null ? plan.getTotalCandidates() : 0;
        int totalAfterSubmit = usedCount + planCount;
        
        // 校验编制人数上限
        if (totalAfterSubmit > config.getHeadcountLimit()) {
            // 先算还差多少
            int overflow = totalAfterSubmit - config.getHeadcountLimit();
            throw new RuntimeException(String.format(
                    "❌ 【编制超限】该单位 %d 年度编制上限为 %d 人，\n" +
                    "已用/在途：%d 人，本次申请：%d 人，超出编制：%d 人。\n" +
                    "请减少招聘人数或联系集团HR申请编制追加。",
                    config.getConfigYear(), config.getHeadcountLimit(),
                    usedCount, planCount, overflow));
        }
        
        // 校验预算上限（如果有预算数据）
        if (config.getBudgetLimit() != null && plan.getTotalBudget() != null
                && plan.getTotalBudget().compareTo(config.getBudgetLimit()) > 0) {
            throw new RuntimeException(String.format(
                    "⚠️ 【预算超限】该单位 %d 年度预算上限为 %.2f 万元，\n" +
                    "本次申请预算 %.2f 万元，超出预算上限。",
                    config.getConfigYear(), config.getBudgetLimit(), plan.getTotalBudget()));
        }
    }

    public RecruitPlanDTO updatePlan(RecruitPlan plan, Long operatorId, String operatorName) {
        RecruitPlan existing = recruitPlanRepository.findById(plan.getId())
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));

        // 状态校验：仅草稿和已退回可编辑
        String status = existing.getStatus();
        if (!"draft".equals(status) && !"rejected".equals(status)) {
            throw new RuntimeException("仅草稿和已退回状态的计划可编辑，当前状态不允许修改");
        }
        
        // ===== 企业级：版本管理 =====
        // 记录修改前的快照，用于生成差异
        RecruitPlan snapshot = clonePlan(existing);
        
        // 更新字段
        existing.setPlanYear(plan.getPlanYear());
        existing.setPlanName(plan.getPlanName());
        if (plan.getOrgUnitId() != null) existing.setOrgUnitId(plan.getOrgUnitId());
        existing.setOrgUnitName(plan.getOrgUnitName());
        existing.setPlanOwner(plan.getPlanOwner());
        existing.setTotalBudget(plan.getTotalBudget());
        existing.setStartDate(plan.getStartDate());
        existing.setEndDate(plan.getEndDate());
        existing.setChannels(plan.getChannels());
        existing.setOtherRequirements(plan.getOtherRequirements());
        existing.setResumeCollectStart(plan.getResumeCollectStart());
        existing.setResumeCollectEnd(plan.getResumeCollectEnd());
        existing.setInterviewMonths(plan.getInterviewMonths());
        existing.setEducationRequired(plan.getEducationRequired());
        existing.setExperienceRequired(plan.getExperienceRequired());
        existing.setCertificatesRequired(plan.getCertificatesRequired());
        existing.setAgeLimit(plan.getAgeLimit());
        existing.setPoliticalStatus(plan.getPoliticalStatus());
        existing.setSpecialRestrictions(plan.getSpecialRestrictions());
        existing.setRecruitmentProcess(plan.getRecruitmentProcess());
        existing.setScoreWeightWritten(plan.getScoreWeightWritten());
        existing.setScoreWeightInterview(plan.getScoreWeightInterview());
        existing.setBackgroundCheckDimensions(plan.getBackgroundCheckDimensions());
        existing.setMedicalStandards(plan.getMedicalStandards());
        existing.setAttachments(plan.getAttachments());
        existing.setInternalNotes(plan.getInternalNotes());
        existing.setCreatorName(plan.getCreatorName());
        // 注意：不覆盖status字段，防止前端篡改状态绕过审批
        // 被驳回的计划重新提交请走 /submit 接口
        
        // 保存计划字段变更（不涉及 positions，cascade 已移除）
        existing.setPositions(null);
        recruitPlanRepository.saveAndFlush(existing);
        
        // 岗位明细 —— 通过 Repository 全权管理
        List<RecruitPosition> newPositions = plan.getPositions();
        if (newPositions != null) {
            // 1. 删除旧岗位
            List<RecruitPosition> oldPositions = recruitPositionRepository.findByPlanId(plan.getId());
            if (oldPositions != null && !oldPositions.isEmpty()) {
                recruitPositionRepository.deleteAllInBatch(oldPositions);
            }
            
            if (!newPositions.isEmpty()) {
                int totalPositions = newPositions.size();
                int totalCandidates = newPositions.stream()
                        .mapToInt(RecruitPosition::getRecruitmentCount)
                        .sum();
                existing.setTotalPositions(totalPositions);
                existing.setTotalCandidates(totalCandidates);
                // 2. 插入新岗位
                for (RecruitPosition pos : newPositions) {
                    pos.setId(null);
                    pos.setPlanId(plan.getId());
                }
                List<RecruitPosition> savedPositions = recruitPositionRepository.saveAll(newPositions);
                existing.setPositions(savedPositions);
            } else {
                existing.setTotalPositions(0);
                existing.setTotalCandidates(0);
            }
            recruitPlanRepository.save(existing);
        }
        
        RecruitPlan updated = recruitPlanRepository.findById(plan.getId())
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));
        
        // 生成版本记录（字段级差异）
        String versionSummary = generateDiffSummary(snapshot, updated);
        int versionNo = planVersionRepository.countByPlanId(plan.getId()) + 1;
        createVersionRecord(updated, snapshot, operatorId, operatorName, versionSummary);
        
        return convertToDTO(updated);
    }
    
    // ===== 版本管理辅助方法 =====

    /**
     * 生成字段级变更差异
     */
    private String generateDiffSummary(RecruitPlan before, RecruitPlan after) {
        List<String> changes = new ArrayList<>();
        
        if (!Objects.equals(before.getPlanName(), after.getPlanName()))
            changes.add("计划名称");
        if (!Objects.equals(before.getTotalCandidates(), after.getTotalCandidates()))
            changes.add(String.format("招聘人数(%d→%d)", 
                    before.getTotalCandidates() != null ? before.getTotalCandidates() : 0,
                    after.getTotalCandidates() != null ? after.getTotalCandidates() : 0));
        if (!Objects.equals(before.getTotalBudget(), after.getTotalBudget()))
            changes.add("招聘预算");
        if (!Objects.equals(before.getStartDate(), after.getStartDate()))
            changes.add("开始日期");
        if (!Objects.equals(before.getEndDate(), after.getEndDate()))
            changes.add("结束日期");
        if (!Objects.equals(before.getStatus(), after.getStatus()))
            changes.add("状态");
            
        return changes.isEmpty() ? "微小调整" : "修改了：" + String.join("、", changes);
    }
    
    /**
     * 创建版本记录
     */
    private void createVersionRecord(RecruitPlan plan, RecruitPlan snapshot, 
                                      Long operatorId, String operatorName, String summary) {
        int versionNo = planVersionRepository.countByPlanId(plan.getId()) + 1;
        
        PlanVersion version = new PlanVersion();
        version.setPlanId(plan.getId());
        version.setVersionNo(versionNo);
        version.setChangeSummary(summary);
        version.setOperatorId(operatorId);
        version.setOperatorName(operatorName);
        
        // 如果有旧数据，生成字段级JSON差异
        if (snapshot != null) {
            Map<String, Object> diff = new LinkedHashMap<>();
            if (!Objects.equals(snapshot.getPlanName(), plan.getPlanName())) {
                Map<String, String> fieldDiff = new LinkedHashMap<>();
                fieldDiff.put("old", snapshot.getPlanName());
                fieldDiff.put("new", plan.getPlanName());
                diff.put("planName", fieldDiff);
            }
            if (!Objects.equals(snapshot.getTotalCandidates(), plan.getTotalCandidates())) {
                Map<String, Object> fieldDiff = new LinkedHashMap<>();
                fieldDiff.put("old", snapshot.getTotalCandidates());
                fieldDiff.put("new", plan.getTotalCandidates());
                diff.put("totalCandidates", fieldDiff);
            }
            if (!Objects.equals(snapshot.getTotalBudget(), plan.getTotalBudget())) {
                Map<String, Object> fieldDiff = new LinkedHashMap<>();
                fieldDiff.put("old", snapshot.getTotalBudget());
                fieldDiff.put("new", plan.getTotalBudget());
                diff.put("totalBudget", fieldDiff);
            }
            // 将diff转为JSON字符串
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                version.setChangedFields(mapper.writeValueAsString(diff));
            } catch (Exception e) {
                // JSON序列化失败时忽略
            }
        }
        
        planVersionRepository.save(version);
    }
    
    /**
     * 克隆一个计划对象（用于版本对比快照）
     */
    private RecruitPlan clonePlan(RecruitPlan source) {
        RecruitPlan clone = new RecruitPlan();
        clone.setPlanName(source.getPlanName());
        clone.setPlanYear(source.getPlanYear());
        clone.setOrgUnitId(source.getOrgUnitId());
        clone.setOrgUnitName(source.getOrgUnitName());
        clone.setPlanOwner(source.getPlanOwner());
        clone.setTotalBudget(source.getTotalBudget());
        clone.setTotalPositions(source.getTotalPositions());
        clone.setTotalCandidates(source.getTotalCandidates());
        clone.setStartDate(source.getStartDate());
        clone.setEndDate(source.getEndDate());
        clone.setChannels(source.getChannels());
        clone.setOtherRequirements(source.getOtherRequirements());
        clone.setResumeCollectStart(source.getResumeCollectStart());
        clone.setResumeCollectEnd(source.getResumeCollectEnd());
        clone.setInterviewMonths(source.getInterviewMonths());
        clone.setEducationRequired(source.getEducationRequired());
        clone.setExperienceRequired(source.getExperienceRequired());
        clone.setCertificatesRequired(source.getCertificatesRequired());
        clone.setAgeLimit(source.getAgeLimit());
        clone.setPoliticalStatus(source.getPoliticalStatus());
        clone.setSpecialRestrictions(source.getSpecialRestrictions());
        clone.setRecruitmentProcess(source.getRecruitmentProcess());
        clone.setScoreWeightWritten(source.getScoreWeightWritten());
        clone.setScoreWeightInterview(source.getScoreWeightInterview());
        clone.setBackgroundCheckDimensions(source.getBackgroundCheckDimensions());
        clone.setMedicalStandards(source.getMedicalStandards());
        clone.setAttachments(source.getAttachments());
        clone.setInternalNotes(source.getInternalNotes());
        clone.setCreatorName(source.getCreatorName());
        clone.setStatus(source.getStatus());
        return clone;
    }
    
    /**
     * 获取计划的历史版本列表
     */
    public List<PlanVersion> getPlanVersions(Long planId) {
        return planVersionRepository.findByPlanIdOrderByVersionNoDesc(planId);
    }

    public List<RecruitPlanDTO> getPlansByYear(Integer year) {
        return recruitPlanRepository.findByPlanYear(year).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RecruitPlanDTO getPlanById(Long id) {
        RecruitPlan plan = recruitPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));
        return convertToDTO(plan);
    }

    public void approvePlan(Long id, String comment) {
        RecruitPlan plan = recruitPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));

        plan.setStatus("approved");
        plan.setApprovalTime(LocalDateTime.now());
        plan.setApprovalComment(comment);
        recruitPlanRepository.save(plan);
    }

    public void rejectPlan(Long id, String comment) {
        RecruitPlan plan = recruitPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));
        
        plan.setStatus("rejected");
        plan.setApprovalTime(LocalDateTime.now());
        plan.setApprovalComment(comment);
        recruitPlanRepository.save(plan);
    }

    public void deletePlan(Long id) {
        RecruitPlan plan = recruitPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));

        // 状态校验：仅草稿可移入已删除
        if (!"draft".equals(plan.getStatus())) {
            throw new RuntimeException("仅草稿状态的计划可删除");
        }

        // 软删除：移入已删除区，可在「已删除」中恢复或彻底移除
        plan.setStatus("deleted");
        recruitPlanRepository.save(plan);
    }

    /**
     * 恢复已删除的计划（回到草稿）
     */
    public void restorePlan(Long id) {
        RecruitPlan plan = recruitPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));
        if (!"deleted".equals(plan.getStatus())) {
            throw new RuntimeException("仅已删除状态的计划可恢复");
        }
        plan.setStatus("draft");
        recruitPlanRepository.save(plan);
    }

    /**
     * 彻底删除（仅对已删除状态的计划执行真正删除）
     */
    public void permanentDelete(Long id) {
        RecruitPlan plan = recruitPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));
        if (!"deleted".equals(plan.getStatus())) {
            throw new RuntimeException("请先将计划移入已删除区后再彻底移除");
        }
        recruitPlanRepository.deleteById(id);
    }

    private RecruitPlanDTO convertToDTO(RecruitPlan plan) {
        RecruitPlanDTO dto = new RecruitPlanDTO();
        dto.setId(plan.getId());
        dto.setPlanYear(plan.getPlanYear());
        dto.setPlanNo(plan.getPlanNo());
        dto.setPlanName(plan.getPlanName());
        dto.setOrgUnitId(plan.getOrgUnitId());
        dto.setOrgUnitName(plan.getOrgUnitName());
        dto.setPlanOwner(plan.getPlanOwner());
        dto.setTotalBudget(plan.getTotalBudget());
        dto.setTotalPositions(plan.getTotalPositions());
        dto.setTotalCandidates(plan.getTotalCandidates());
        dto.setStatus(plan.getStatus());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setChannels(plan.getChannels());
        dto.setOtherRequirements(plan.getOtherRequirements());
        dto.setResumeCollectStart(plan.getResumeCollectStart());
        dto.setResumeCollectEnd(plan.getResumeCollectEnd());
        dto.setInterviewMonths(plan.getInterviewMonths());
        dto.setEducationRequired(plan.getEducationRequired());
        dto.setExperienceRequired(plan.getExperienceRequired());
        dto.setCertificatesRequired(plan.getCertificatesRequired());
        dto.setAgeLimit(plan.getAgeLimit());
        dto.setPoliticalStatus(plan.getPoliticalStatus());
        dto.setSpecialRestrictions(plan.getSpecialRestrictions());
        dto.setRecruitmentProcess(plan.getRecruitmentProcess());
        dto.setScoreWeightWritten(plan.getScoreWeightWritten());
        dto.setScoreWeightInterview(plan.getScoreWeightInterview());
        dto.setBackgroundCheckDimensions(plan.getBackgroundCheckDimensions());
        dto.setMedicalStandards(plan.getMedicalStandards());
        dto.setAttachments(plan.getAttachments());
        dto.setInternalNotes(plan.getInternalNotes());
        dto.setCreatorName(plan.getCreatorName());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setSubmitTime(plan.getSubmitTime());
        dto.setApprovalTime(plan.getApprovalTime());
        dto.setApprovalComment(plan.getApprovalComment());
        // 展示字段：与前端使用保持一致
        dto.setHeadcount(plan.getTotalCandidates());
        dto.setLeader(plan.getPlanOwner());
        dto.setRejectionReason(plan.getApprovalComment());
        dto.setApprover(queryApprover(plan.getId()));
        
        // 转换岗位列表
        if (plan.getPositions() != null) {
            dto.setPositions(plan.getPositions().stream()
                    .map(this::convertPositionToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    /** 查询最近一条审批通过的审批人姓名 */
    private String queryApprover(Long planId) {
        try {
            List<String> names = jdbcTemplate.query(
                "SELECT u.real_name FROM process_approval_record r " +
                "JOIN process_approval a ON a.id = r.approval_id " +
                "JOIN sys_user u ON u.id = r.approver_id " +
                "WHERE a.process_type = 'recruit_plan' AND a.process_id = ? AND r.action = 'approve' " +
                "ORDER BY r.created_at DESC LIMIT 1",
                (rs, rowNum) -> rs.getString(1),
                planId);
            return names.isEmpty() ? null : names.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private RecruitPositionDTO convertPositionToDTO(RecruitPosition position) {
        RecruitPositionDTO dto = new RecruitPositionDTO();
        dto.setId(position.getId());
        dto.setPlanId(position.getPlanId());
        dto.setPositionName(position.getPositionName());
        dto.setMajorRequired(position.getMajorRequired());
        dto.setPositionCategory(position.getPositionCategory());
        dto.setRecruitmentCount(position.getRecruitmentCount());
        dto.setEducationLevel(position.getEducationLevel());
        dto.setQualificationReq(position.getQualificationReq());
        dto.setStatus(position.getStatus());
        dto.setDeptName(position.getDeptName());
        dto.setRank(position.getRank());
        dto.setWorkLocation(position.getWorkLocation());
        dto.setSalaryRange(position.getSalaryRange());
        dto.setEmploymentType(position.getEmploymentType());
        dto.setJobResponsibility(position.getJobResponsibility());
        dto.setJobRequirement(position.getJobRequirement());
        return dto;
    }
}
