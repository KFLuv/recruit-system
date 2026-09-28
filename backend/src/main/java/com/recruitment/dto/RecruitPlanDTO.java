package com.recruitment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecruitPlanDTO {
    private Long id;
    private Integer planYear;
    private String planNo;
    private String planName;
    private Long orgUnitId;
    private String orgUnitName;
    private String planOwner;
    private BigDecimal totalBudget;
    private Integer totalPositions;
    private Integer totalCandidates;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String channels;
    private String otherRequirements;
    private LocalDate resumeCollectStart;
    private LocalDate resumeCollectEnd;
    private String interviewMonths;
    private String educationRequired;
    private String experienceRequired;
    private String certificatesRequired;
    private String ageLimit;
    private String politicalStatus;
    private String specialRestrictions;
    private String recruitmentProcess;
    private Double scoreWeightWritten;
    private Double scoreWeightInterview;
    private String backgroundCheckDimensions;
    private String medicalStandards;
    private String attachments;
    private String internalNotes;
    private String creatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalTime;
    private String approvalComment;
    /** 展示用：招聘人数（= totalCandidates） */
    private Integer headcount;
    /** 展示用：负责人（= planOwner） */
    private String leader;
    /** 展示用：审批人 */
    private String approver;
    /** 展示用：驳回原因（= approvalComment） */
    private String rejectionReason;
    private List<RecruitPositionDTO> positions;
}
