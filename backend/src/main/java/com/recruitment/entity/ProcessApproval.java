package com.recruitment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "process_approval")
public class ProcessApproval extends BaseEntity {

    @Column(name = "process_type", nullable = false)
    private String processType;

    @Column(name = "process_id", nullable = false)
    private Long processId;

    @Column(name = "current_node")
    private String currentNode;

    private String status = "pending";

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "approval_nodes", length = 500)
    private String approvalNodes;   // 完整审批链路，如 "二级单位审批,集团HR审批,集团领导审批"
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProcessType() { return processType; }
    public void setProcessType(String processType) { this.processType = processType; }
    public Long getProcessId() { return processId; }
    public void setProcessId(Long processId) { this.processId = processId; }
    public String getCurrentNode() { return currentNode; }
    public void setCurrentNode(String currentNode) { this.currentNode = currentNode; }
    public String getApprovalNodes() { return approvalNodes; }
    public void setApprovalNodes(String approvalNodes) { this.approvalNodes = approvalNodes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
