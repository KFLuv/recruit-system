package com.recruitment.service;

import com.recruitment.dto.ApprovalDTO;
import com.recruitment.entity.ProcessApproval;
import com.recruitment.entity.ProcessApprovalRecord;
import com.recruitment.entity.RecruitPlan;
import com.recruitment.repository.ProcessApprovalRepository;
import com.recruitment.repository.ProcessApprovalRecordRepository;
import com.recruitment.repository.RecruitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProcessApprovalService {

    @Autowired
    private ProcessApprovalRepository processApprovalRepository;

    @Autowired
    private ProcessApprovalRecordRepository processApprovalRecordRepository;
    
    @Autowired
    private RecruitPlanRepository recruitPlanRepository;

    /**
     * 审批节点配置 —— 按单位层级决定审批链路
     * 集团总部：集团 HR 审批 → 集团领导审批
     * 二级单位：二级单位审批 → 集团 HR 审批 → 集团领导审批
     */
    private static final Map<Long, String[]> APPROVAL_NODES = new HashMap<>();
    static {
        APPROVAL_NODES.put(1L, new String[]{"集团 HR 审批", "集团领导审批"});           // 集团总部
        APPROVAL_NODES.put(2L, new String[]{"二级单位审批", "集团 HR 审批", "集团领导审批"}); // 二级单位
        APPROVAL_NODES.put(3L, new String[]{"二级单位审批", "集团 HR 审批", "集团领导审批"}); // 二级单位
    }

    // 流程类型中文名映射
    private static final Map<String, String> TYPE_NAMES = new HashMap<>();
    static {
        TYPE_NAMES.put("recruit_plan", "招聘计划审批");
        TYPE_NAMES.put("position", "岗位审批");
        TYPE_NAMES.put("candidate", "候选人审批");
    }
    
    // 状态中文名映射
    private static final Map<String, String> STATUS_NAMES = new HashMap<>();
    static {
        STATUS_NAMES.put("pending", "待处理");
        STATUS_NAMES.put("approved", "已通过");
        STATUS_NAMES.put("rejected", "已驳回");
    }

    /**
     * 创建简单审批流程（兼容旧接口）
     */
    public ProcessApproval createApproval(String processType, Long processId, Long creatorId) {
        ProcessApproval approval = new ProcessApproval();
        approval.setProcessType(processType);
        approval.setProcessId(processId);
        approval.setCurrentNode("待审批");
        approval.setStatus("pending");
        approval.setCreatorId(creatorId);
        return processApprovalRepository.save(approval);
    }

    /**
     * 【企业级】创建多级审批流程
     * 根据申报单位的层级自动决定审批链路
     */
    public ProcessApproval createMultiLevelApproval(String processType, Long processId,
                                                     Long creatorId, Long orgUnitId) {
        // 确定该单位对应的审批链路
        String[] nodes = APPROVAL_NODES.getOrDefault(orgUnitId,
                new String[]{"二级单位审批", "集团HR审批", "集团领导审批"});

        ProcessApproval approval = new ProcessApproval();
        approval.setProcessType(processType);
        approval.setProcessId(processId);
        approval.setCurrentNode(nodes[0]);          // 从第一个节点开始
        approval.setApprovalNodes(String.join(",", nodes)); // 存储完整审批链路
        approval.setStatus("pending");
        approval.setCreatorId(creatorId);

        return processApprovalRepository.save(approval);
    }

    public List<ApprovalDTO> getPendingApprovals(String processType) {
        List<ProcessApproval> approvals = processApprovalRepository.findByProcessTypeAndStatus(processType, "pending");
        return approvals.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ApprovalDTO> getAllApprovals(String processType) {
        List<ProcessApproval> approvals = processApprovalRepository.findByProcessTypeOrderByCreatedAtDesc(processType);
        return approvals.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProcessApproval getApprovalById(Long id) {
        return processApprovalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("审批流程不存在"));
    }
    
    /**
     * 将审批记录转为用户友好的 DTO
     */
    private ApprovalDTO toDTO(ProcessApproval approval) {
        ApprovalDTO dto = new ApprovalDTO();
        dto.setId(approval.getId());
        dto.setProcessType(approval.getProcessType());
        dto.setProcessTypeName(TYPE_NAMES.getOrDefault(approval.getProcessType(), approval.getProcessType()));
        dto.setProcessId(approval.getProcessId());
        dto.setCurrentNode(approval.getCurrentNode());
        dto.setStatus(approval.getStatus());
        dto.setStatusName(STATUS_NAMES.getOrDefault(approval.getStatus(), approval.getStatus()));
        dto.setCreatorId(approval.getCreatorId());
        dto.setCreatedAt(approval.getCreatedAt());
        
        // 根据流程类型获取关联内容的标题和详情
        if ("recruit_plan".equals(approval.getProcessType())) {
            recruitPlanRepository.findById(approval.getProcessId()).ifPresentOrElse(plan -> {
                dto.setProcessTitle(plan.getPlanName());
                String count = plan.getTotalCandidates() != null ? plan.getTotalCandidates() + "人" : "待定";
                dto.setDetail(plan.getPlanYear() + "年度 · " + plan.getPlanName() + "（招聘" + count + "）");
            }, () -> {
                dto.setProcessTitle("（计划已删除）");
                dto.setDetail("该招聘计划已被删除");
            });
        }
        
        return dto;
    }

    public ProcessApproval approve(Long id, Long approverId, String comment) {
        ProcessApproval approval = getApprovalById(id);
        
        // 记录本次审批操作
        ProcessApprovalRecord record = new ProcessApprovalRecord();
        record.setApprovalId(id);
        record.setApproverId(approverId);
        record.setAction("approve");
        record.setComment(comment);
        
        // ===== 多级审批流转 =====
        // 检查是否还有下一级审批节点
        String nodes = approval.getApprovalNodes();
        if (nodes != null && !nodes.isEmpty()) {
            String[] nodeArray = nodes.split(",");
            String current = approval.getCurrentNode();
            int currentIndex = -1;
            for (int i = 0; i < nodeArray.length; i++) {
                if (nodeArray[i].equals(current)) {
                    currentIndex = i;
                    break;
                }
            }
            // 如果还有下一级，流转到下一节点；否则审批通过
            if (currentIndex >= 0 && currentIndex < nodeArray.length - 1) {
                approval.setCurrentNode(nodeArray[currentIndex + 1]);
                approval.setStatus("pending");  // 仍然是待处理（下一节点待审）
            } else {
                approval.setStatus("approved");
            }
        } else {
            // 没有配置多级节点时，直接通过
            approval.setStatus("approved");
        }
        
        ProcessApproval saved = processApprovalRepository.save(approval);
        
        // 如果是招聘计划审批，根据最终状态更新计划
        if ("recruit_plan".equals(approval.getProcessType())) {
            updateRecruitPlanStatus(approval.getProcessId(), approval.getStatus(), comment);
        }
        
        processApprovalRecordRepository.save(record);
        return saved;
    }

    public ProcessApproval reject(Long id, Long approverId, String comment) {
        ProcessApproval approval = getApprovalById(id);
        
        ProcessApprovalRecord record = new ProcessApprovalRecord();
        record.setApprovalId(id);
        record.setApproverId(approverId);
        record.setAction("reject");
        record.setComment(comment);
        
        // 驳回：直接退回草稿状态，审批终止
        approval.setStatus("rejected");
        
        // 如果是招聘计划审批，更新计划状态为退回
        if ("recruit_plan".equals(approval.getProcessType())) {
            updateRecruitPlanStatus(approval.getProcessId(), "rejected", comment);
        }
        
        ProcessApproval saved = processApprovalRepository.save(approval);
        
        processApprovalRecordRepository.save(record);
        return saved;
    }
    
    /**
     * 根据审批状态更新招聘计划状态
     */
    private void updateRecruitPlanStatus(Long planId, String approvalStatus, String comment) {
        RecruitPlan plan = recruitPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("招聘计划不存在"));
        
        if ("approved".equals(approvalStatus)) {
            plan.setStatus("approved");
            plan.setApprovalTime(LocalDateTime.now());
            plan.setApprovalComment(comment);
        } else if ("rejected".equals(approvalStatus)) {
            plan.setStatus("rejected");   // 保持已退回状态，可在计划列表重新提交
            plan.setApprovalTime(LocalDateTime.now());
            plan.setApprovalComment(comment);
        }
        // pending（流转中）不更新计划状态
        recruitPlanRepository.save(plan);
    }

    public void cancelApproval(Long id) {
        processApprovalRepository.deleteById(id);
    }
}
