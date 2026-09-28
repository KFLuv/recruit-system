package com.recruitment.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CandidateDTO {
    private Long id;
    private String candidateNo;
    private String realName;
    private String gender;
    private Integer age;
    private String education;
    private String university;
    private String major;
    private String phone;
    private String email;
    private Long appliedPositionId;
    private String appliedPosition;  // 应聘岗位名称
    private String resumeSource;     // 简历来源
    private String status;
    private String rejectionReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String resumeFilePath;
    private Double scoreWritten;
    private Double scoreInterview;
    private Double scoreTotal;
    private String rating;           // 综合评级
    private String commentWritten;   // 笔试评语
    private String commentInterview; // 面试评语
    private String interviewer;      // 面试官
    private String remark;
    private String idNumber;
    private String domicile;
    private String currentAddress;
    private Long planId;
    private String planName;
    private String orgUnitName;
    private Double workYears;
    private String expectedSalary;
    private String availableDate;
    private String emergencyContact;
    private String emergencyPhone;
    private Double scoreFirstInterview;
    private Double scoreSecondInterview;
    private String interviewerFirst;
    private String interviewerSecond;
    private String commentFirstInterview;
    private String commentSecondInterview;
    private String screeningResult;
    private String interviewSchedule;
    private String medicalResult;
    private String backgroundCheckResult;
    private String hireConclusion;
    private String certificates;
    private String awards;
    private String selfEvaluation;
    
    // 工作经历
    private List<WorkExperienceDTO> workExperiences;
    
    // 项目经历
    private List<ProjectExperienceDTO> projectExperiences;
}
