package com.recruitment.controller;

import com.recruitment.dto.RecruitPlanDTO;
import com.recruitment.entity.RecruitPlan;
import com.recruitment.entity.RecruitPosition;
import com.recruitment.service.RecruitPlanService;
import com.recruitment.util.PermissionUtil;
import com.recruitment.dto.UserDTO;
import com.recruitment.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recruit-plans")
public class RecruitPlanController {

    @Autowired
    private RecruitPlanService recruitPlanService;

    @PostMapping
    @Log(module = "招聘计划", description = "创建计划")
    public Map<String, Object> createPlan(@RequestBody Map<String, Object> planData, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            RecruitPlan plan = convertToEntity(planData);
            RecruitPlanDTO created = recruitPlanService.createPlan(plan, user.getId(), user.getRealName());
            result.put("success", true);
            result.put("data", created);
            result.put("message", "创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping
    public Map<String, Object> getAllPlans(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long orgUnitId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<RecruitPlanDTO> plans = recruitPlanService.getAllPlans(year, orgUnitId, status, keyword);
            result.put("success", true);
            result.put("data", plans);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<RecruitPlan> all = recruitPlanService.findAllEntities().stream()
                    .filter(p -> !"deleted".equals(p.getStatus()))
                    .collect(Collectors.toList());
            long totalPlans = all.size();
            long draftCount = all.stream().filter(p -> "draft".equals(p.getStatus())).count();
            long submittingCount = all.stream().filter(p -> "submitting".equals(p.getStatus())).count();
            long approvedCount = all.stream().filter(p -> "approved".equals(p.getStatus())).count();
            long rejectedCount = all.stream().filter(p -> "rejected".equals(p.getStatus())).count();
            long archivedCount = all.stream().filter(p -> "archived".equals(p.getStatus())).count();
            double totalBudget = all.stream()
                    .filter(p -> p.getTotalBudget() != null)
                    .mapToDouble(p -> p.getTotalBudget().doubleValue()).sum();
            long totalHeadcount = all.stream()
                    .filter(p -> p.getTotalPositions() != null)
                    .mapToLong(RecruitPlan::getTotalPositions).sum();
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPlans", totalPlans);
            stats.put("draftCount", draftCount);
            stats.put("submittingCount", submittingCount);
            stats.put("approvedCount", approvedCount);
            stats.put("rejectedCount", rejectedCount);
            stats.put("archivedCount", archivedCount);
            stats.put("totalBudget", totalBudget);
            stats.put("totalHeadcount", totalHeadcount);
            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/batch/submit")
    public Map<String, Object> batchSubmit(@RequestBody List<Long> planIds, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            recruitPlanService.batchSubmit(planIds);
            result.put("success", true);
            result.put("message", "批量提交成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/batch/revoke")
    public Map<String, Object> batchRevoke(@RequestBody List<Long> planIds, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            recruitPlanService.batchRevoke(planIds);
            result.put("success", true);
            result.put("message", "批量作废成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}/versions")
    public Map<String, Object> getPlanVersions(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("data", recruitPlanService.getPlanVersions(id));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getPlanById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            RecruitPlanDTO plan = recruitPlanService.getPlanById(id);
            result.put("success", true);
            result.put("data", plan);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/year/{year}")
    public Map<String, Object> getPlansByYear(@PathVariable Integer year) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<RecruitPlanDTO> plans = recruitPlanService.getPlansByYear(year);
            result.put("success", true);
            result.put("data", plans);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}")
    @Log(module = "招聘计划", description = "更新计划")
    public Map<String, Object> updatePlan(@PathVariable Long id, @RequestBody Map<String, Object> planData, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            RecruitPlan plan = convertToEntity(planData);
            plan.setId(id);
            RecruitPlanDTO updated = recruitPlanService.updatePlan(plan, user.getId(), user.getRealName());
            result.put("success", true);
            result.put("data", updated);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/submit")
    @Log(module = "招聘计划", description = "提交审批")
    public Map<String, Object> submitPlan(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            recruitPlanService.submitPlan(id, user.getId(), user.getRealName());
            result.put("success", true);
            result.put("data", recruitPlanService.getPlanById(id));
            result.put("message", "提交成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    @Log(module = "招聘计划", description = "移入已删除")
    public Map<String, Object> deletePlan(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            RecruitPlanDTO plan = recruitPlanService.getPlanById(id);
            recruitPlanService.deletePlan(id);
            result.put("success", true);
            result.put("data", plan);
            result.put("message", "已移入已删除");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/restore")
    @Log(module = "招聘计划", description = "恢复计划")
    public Map<String, Object> restorePlan(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            recruitPlanService.restorePlan(id);
            result.put("success", true);
            result.put("data", recruitPlanService.getPlanById(id));
            result.put("message", "已恢复为草稿");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}/permanent")
    @Log(module = "招聘计划", description = "彻底删除")
    public Map<String, Object> permanentDeletePlan(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            RecruitPlanDTO plan = recruitPlanService.getPlanById(id);
            recruitPlanService.permanentDelete(id);
            result.put("success", true);
            result.put("data", plan);
            result.put("message", "已彻底删除");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/revoke")
    public Map<String, Object> revokePlan(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");

            recruitPlanService.batchRevoke(java.util.Collections.singletonList(id));
            result.put("success", true);
            result.put("data", recruitPlanService.getPlanById(id));
            result.put("message", "已作废");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/approve")
    @Log(module = "招聘计划", description = "审批通过")
    public Map<String, Object> approvePlan(@PathVariable Long id, @RequestBody Map<String, String> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            String comment = params.get("comment");
            recruitPlanService.approvePlan(id, comment);
            result.put("success", true);
            result.put("data", recruitPlanService.getPlanById(id));
            result.put("message", "审批通过");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/reject")
    @Log(module = "招聘计划", description = "审批驳回")
    public Map<String, Object> rejectPlan(@PathVariable Long id, @RequestBody Map<String, String> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            String comment = params.get("comment");
            recruitPlanService.rejectPlan(id, comment);
            result.put("success", true);
            result.put("data", recruitPlanService.getPlanById(id));
            result.put("message", "审批驳回");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    // 辅助方法：Map 转 Entity
    private RecruitPlan convertToEntity(Map<String, Object> data) {
        RecruitPlan plan = new RecruitPlan();
        
        // 兼容 Integer 和 String 类型
        Object planYearObj = data.get("planYear");
        if (planYearObj != null) {
            String planYearStr = planYearObj.toString().trim();
            if (!planYearStr.isEmpty()) {
                if (planYearObj instanceof String) {
                    plan.setPlanYear(Integer.parseInt(planYearStr));
                } else {
                    plan.setPlanYear(((Number) planYearObj).intValue());
                }
            }
        }
        
        plan.setPlanName((String) data.get("planName"));
        
        Object orgUnitIdObj = data.get("orgUnitId");
        if (orgUnitIdObj != null) {
            String orgUnitIdStr = orgUnitIdObj.toString().trim();
            if (!orgUnitIdStr.isEmpty()) {
                if (orgUnitIdObj instanceof String) {
                    plan.setOrgUnitId(Long.parseLong(orgUnitIdStr));
                } else {
                    plan.setOrgUnitId(((Number) orgUnitIdObj).longValue());
                }
            }
        }
        
        plan.setOrgUnitName((String) data.get("orgUnitName"));
        plan.setPlanOwner((String) data.get("planOwner"));
        plan.setStatus((String) data.get("status"));
        
        Object budgetObj = data.get("totalBudget");
        if (budgetObj != null) {
            String budgetStr = budgetObj.toString().trim();
            if (!budgetStr.isEmpty()) {
                if (budgetObj instanceof String) {
                    plan.setTotalBudget(new java.math.BigDecimal(budgetStr));
                } else {
                    plan.setTotalBudget(new java.math.BigDecimal(budgetObj.toString()));
                }
            }
        }
        
        Object positionsObj = data.get("totalPositions");
        if (positionsObj != null) {
            String positionsStr = positionsObj.toString().trim();
            if (!positionsStr.isEmpty()) {
                if (positionsObj instanceof String) {
                    plan.setTotalPositions(Integer.parseInt(positionsStr));
                } else {
                    plan.setTotalPositions(((Number) positionsObj).intValue());
                }
            }
        }
        
        Object candidatesObj = data.get("totalCandidates");
        if (candidatesObj != null) {
            String candidatesStr = candidatesObj.toString().trim();
            if (!candidatesStr.isEmpty()) {
                if (candidatesObj instanceof String) {
                    plan.setTotalCandidates(Integer.parseInt(candidatesStr));
                } else {
                    plan.setTotalCandidates(((Number) candidatesObj).intValue());
                }
            }
        }
        
        Object startDateObj = data.get("startDate");
        if (startDateObj != null) {
            String startDateStr = startDateObj.toString().trim();
            if (!startDateStr.isEmpty()) {
                plan.setStartDate(LocalDate.parse(startDateStr));
            }
        }
        
        Object endDateObj = data.get("endDate");
        if (endDateObj != null) {
            String endDateStr = endDateObj.toString().trim();
            if (!endDateStr.isEmpty()) {
                plan.setEndDate(LocalDate.parse(endDateStr));
            }
        }
        
        plan.setChannels((String) data.get("channels"));
        plan.setOtherRequirements((String) data.get("otherRequirements"));
        Object resumeStartObj = data.get("resumeCollectStart");
        if (resumeStartObj != null) {
            String resumeStartStr = resumeStartObj.toString().trim();
            if (!resumeStartStr.isEmpty()) plan.setResumeCollectStart(LocalDate.parse(resumeStartStr));
        }
        Object resumeEndObj = data.get("resumeCollectEnd");
        if (resumeEndObj != null) {
            String resumeEndStr = resumeEndObj.toString().trim();
            if (!resumeEndStr.isEmpty()) plan.setResumeCollectEnd(LocalDate.parse(resumeEndStr));
        }
        plan.setInterviewMonths((String) data.get("interviewMonths"));
        plan.setEducationRequired((String) data.get("educationRequired"));
        plan.setExperienceRequired((String) data.get("experienceRequired"));
        plan.setCertificatesRequired((String) data.get("certificatesRequired"));
        plan.setAgeLimit((String) data.get("ageLimit"));
        plan.setPoliticalStatus((String) data.get("politicalStatus"));
        plan.setSpecialRestrictions((String) data.get("specialRestrictions"));
        plan.setRecruitmentProcess((String) data.get("recruitmentProcess"));
        Object ww = data.get("scoreWeightWritten");
        if (ww instanceof Number) plan.setScoreWeightWritten(((Number) ww).doubleValue());
        Object wi = data.get("scoreWeightInterview");
        if (wi instanceof Number) plan.setScoreWeightInterview(((Number) wi).doubleValue());
        plan.setBackgroundCheckDimensions((String) data.get("backgroundCheckDimensions"));
        plan.setMedicalStandards((String) data.get("medicalStandards"));
        plan.setInternalNotes((String) data.get("internalNotes"));
        plan.setCreatorName((String) data.get("creatorName"));
        
        // 处理岗位明细
        Object posListObj = data.get("positions");
        if (posListObj instanceof List<?>) {
            List<?> rawList = (List<?>) posListObj;
            List<RecruitPosition> positions = new java.util.ArrayList<>();
            for (Object item : rawList) {
                if (!(item instanceof Map<?, ?>)) continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> posData = (Map<String, Object>) item;
                RecruitPosition pos = new RecruitPosition();
                // 兼容前端发送的 deptName 键（v-model="pos.deptName"），历史数据可能用 department
                Object deptVal = posData.get("deptName");
                if (deptVal == null) deptVal = posData.get("department");
                pos.setDeptName(deptVal != null ? deptVal.toString() : null);
                pos.setPositionName((String) posData.get("positionName"));
                Object countObj = posData.get("recruitmentCount");
                if (countObj instanceof Number) pos.setRecruitmentCount(((Number) countObj).intValue());
                pos.setRank((String) posData.get("rank"));
                pos.setWorkLocation((String) posData.get("workLocation"));
                pos.setSalaryRange((String) posData.get("salaryRange"));
                pos.setEmploymentType((String) posData.get("employmentType"));
                pos.setEducationLevel((String) posData.get("educationLevel"));
                pos.setMajorRequired((String) posData.get("majorRequired"));
                pos.setQualificationReq((String) posData.get("qualificationReq"));
                pos.setExperienceReq((String) posData.get("experienceReq"));
                Object ageMinObj = posData.get("ageMin");
                if (ageMinObj instanceof Number) pos.setAgeMin(((Number) ageMinObj).intValue());
                Object ageMaxObj = posData.get("ageMax");
                if (ageMaxObj instanceof Number) pos.setAgeMax(((Number) ageMaxObj).intValue());
                pos.setPoliticalReq((String) posData.get("politicalReq"));
                pos.setSpecialReq((String) posData.get("specialReq"));
                pos.setJobResponsibility((String) posData.get("jobResponsibility"));
                pos.setJobRequirement((String) posData.get("jobRequirement"));
                positions.add(pos);
            }
            plan.setPositions(positions);
        }
        
        return plan;
    }
}
