package com.recruitment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ApprovalDTO {
    private Long id;
    private String processType;
    private String processTypeName;  // 流程类型中文名
    private Long processId;
    private String processTitle;     // 关联内容的标题
    private String currentNode;
    private String status;
    private String statusName;
    private Long creatorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String detail;           // 详情描述

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProcessType() { return processType; }
    public void setProcessType(String processType) { this.processType = processType; }
    public String getProcessTypeName() { return processTypeName; }
    public void setProcessTypeName(String processTypeName) { this.processTypeName = processTypeName; }
    public Long getProcessId() { return processId; }
    public void setProcessId(Long processId) { this.processId = processId; }
    public String getProcessTitle() { return processTitle; }
    public void setProcessTitle(String processTitle) { this.processTitle = processTitle; }
    public String getCurrentNode() { return currentNode; }
    public void setCurrentNode(String currentNode) { this.currentNode = currentNode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}
