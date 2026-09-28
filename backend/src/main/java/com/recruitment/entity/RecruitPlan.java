package com.recruitment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recruit_plan")
public class RecruitPlan extends BaseEntity {

    @Column(name = "plan_year")
    private Integer planYear;

    @Column(name = "plan_no")
    private String planNo;               // 计划编号（如 PL20260001）

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "org_unit_id")
    private Long orgUnitId;

    @Column(name = "org_unit_name")
    private String orgUnitName;

    @Column(name = "plan_owner")
    private String planOwner;

    @Column(name = "status")
    private String status;

    @Column(name = "total_budget")
    private BigDecimal totalBudget;

    @Column(name = "total_positions")
    private Integer totalPositions;

    @Column(name = "total_candidates")
    private Integer totalCandidates;

    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "channels")
    private String channels;

    @Column(name = "other_requirements")
    private String otherRequirements;

    @Column(name = "resume_collect_start")
    private LocalDate resumeCollectStart;        // 简历收集开始日期

    @Column(name = "resume_collect_end")
    private LocalDate resumeCollectEnd;          // 简历收集结束日期

    @Column(name = "interview_months")
    private String interviewMonths;              // 集中面试月份安排

    @Column(name = "education_required")
    private String educationRequired;            // 学历要求

    @Column(name = "experience_required")
    private String experienceRequired;           // 工作经验年限要求

    @Column(name = "certificates_required")
    private String certificatesRequired;         // 必备证书/资质

    @Column(name = "age_limit")
    private String ageLimit;                     // 年龄限制

    @Column(name = "political_status")
    private String politicalStatus;              // 政治面貌要求

    @Column(name = "special_restrictions")
    private String specialRestrictions;          // 特殊限制

    @Column(name = "recruitment_process")
    private String recruitmentProcess;           // 招聘流程

    @Column(name = "score_weight_written")
    private Double scoreWeightWritten;           // 笔试成绩占比

    @Column(name = "score_weight_interview")
    private Double scoreWeightInterview;         // 面试成绩占比

    @Column(name = "background_check_dimensions")
    private String backgroundCheckDimensions;    // 背调核查维度

    @Column(name = "medical_standards")
    private String medicalStandards;             // 体检标准

    @Column(name = "attachments")
    private String attachments;                  // 附件路径

    @Column(name = "internal_notes")
    private String internalNotes;                // 内部备注

    @Column(name = "creator_name")
    private String creatorName;                  // 创建人名称

    @Column(name = "submit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @Column(name = "approval_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalTime;

    @Column(name = "approval_comment")
    private String approvalComment;

    @Column(name = "creator_id")
    private Long creatorId;

    @OneToMany(mappedBy = "planId")
    private List<RecruitPosition> positions;
    
    // Getters and Setters
    public Integer getPlanYear() { return planYear; }
    public void setPlanYear(Integer planYear) { this.planYear = planYear; }
    public String getPlanNo() { return planNo; }
    public void setPlanNo(String planNo) { this.planNo = planNo; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public Long getOrgUnitId() { return orgUnitId; }
    public void setOrgUnitId(Long orgUnitId) { this.orgUnitId = orgUnitId; }
    public Integer getTotalPositions() { return totalPositions; }
    public void setTotalPositions(Integer totalPositions) { this.totalPositions = totalPositions; }
    public Integer getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(Integer totalCandidates) { this.totalCandidates = totalCandidates; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getApprovalTime() { return approvalTime; }
    public void setApprovalTime(LocalDateTime approvalTime) { this.approvalTime = approvalTime; }
    public String getApprovalComment() { return approvalComment; }
    public void setApprovalComment(String approvalComment) { this.approvalComment = approvalComment; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public List<RecruitPosition> getPositions() { return positions; }
    public void setPositions(List<RecruitPosition> positions) { this.positions = positions; }
    
    // Getters and Setters for new fields
    public String getOrgUnitName() { return orgUnitName; }
    public void setOrgUnitName(String orgUnitName) { this.orgUnitName = orgUnitName; }
    
    public String getPlanOwner() { return planOwner; }
    public void setPlanOwner(String planOwner) { this.planOwner = planOwner; }
    
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getChannels() { return channels; }
    public void setChannels(String channels) { this.channels = channels; }
    
    public String getOtherRequirements() { return otherRequirements; }
    public void setOtherRequirements(String otherRequirements) { this.otherRequirements = otherRequirements; }
    public LocalDate getResumeCollectStart() { return resumeCollectStart; }
    public void setResumeCollectStart(LocalDate resumeCollectStart) { this.resumeCollectStart = resumeCollectStart; }
    public LocalDate getResumeCollectEnd() { return resumeCollectEnd; }
    public void setResumeCollectEnd(LocalDate resumeCollectEnd) { this.resumeCollectEnd = resumeCollectEnd; }
    public String getInterviewMonths() { return interviewMonths; }
    public void setInterviewMonths(String interviewMonths) { this.interviewMonths = interviewMonths; }
    public String getEducationRequired() { return educationRequired; }
    public void setEducationRequired(String educationRequired) { this.educationRequired = educationRequired; }
    public String getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(String experienceRequired) { this.experienceRequired = experienceRequired; }
    public String getCertificatesRequired() { return certificatesRequired; }
    public void setCertificatesRequired(String certificatesRequired) { this.certificatesRequired = certificatesRequired; }
    public String getAgeLimit() { return ageLimit; }
    public void setAgeLimit(String ageLimit) { this.ageLimit = ageLimit; }
    public String getPoliticalStatus() { return politicalStatus; }
    public void setPoliticalStatus(String politicalStatus) { this.politicalStatus = politicalStatus; }
    public String getSpecialRestrictions() { return specialRestrictions; }
    public void setSpecialRestrictions(String specialRestrictions) { this.specialRestrictions = specialRestrictions; }
    public String getRecruitmentProcess() { return recruitmentProcess; }
    public void setRecruitmentProcess(String recruitmentProcess) { this.recruitmentProcess = recruitmentProcess; }
    public Double getScoreWeightWritten() { return scoreWeightWritten; }
    public void setScoreWeightWritten(Double scoreWeightWritten) { this.scoreWeightWritten = scoreWeightWritten; }
    public Double getScoreWeightInterview() { return scoreWeightInterview; }
    public void setScoreWeightInterview(Double scoreWeightInterview) { this.scoreWeightInterview = scoreWeightInterview; }
    public String getBackgroundCheckDimensions() { return backgroundCheckDimensions; }
    public void setBackgroundCheckDimensions(String backgroundCheckDimensions) { this.backgroundCheckDimensions = backgroundCheckDimensions; }
    public String getMedicalStandards() { return medicalStandards; }
    public void setMedicalStandards(String medicalStandards) { this.medicalStandards = medicalStandards; }
    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
}
