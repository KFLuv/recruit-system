package com.recruitment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "candidate")
public class Candidate extends BaseEntity {

    @Column(name = "candidate_no", unique = true)
    private String candidateNo;

    @Column(name = "real_name", nullable = false)
    private String realName;

    private String gender;

    private Integer age;

    private String education;

    @Column(name = "university")
    private String university;

    private String major;

    private String phone;

    private String email;

    @Column(name = "applied_position_id")
    private Long appliedPositionId;
    
    @Column(name = "applied_position")
    private String appliedPosition;

    private String resumeSource;

    @Column(name = "status")
    private String status = "new";

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;          // 淘汰原因

    @Column(name = "resume_file_path")
    private String resumeFilePath;

    // 成绩模块 - 拆分录入
    @Column(name = "score_written")
    private Double scoreWritten;
    
    @Column(name = "comment_written")
    private String commentWritten;
    
    @Column(name = "score_interview")
    private Double scoreInterview;
    
    @Column(name = "interviewer")
    private String interviewer;
    
    @Column(name = "comment_interview")
    private String commentInterview;
    
    @Column(name = "score_total")
    private Double scoreTotal;
    
    @Column(name = "rating")
    private String rating; // A/B/C/D

    private String remark;

    @Column(name = "id_number")
    private String idNumber;           // 身份证号（脱敏展示）

    @Column(name = "domicile")
    private String domicile;           // 户籍所在地

    @Column(name = "current_address")
    private String currentAddress;     // 现居住地址

    @Column(name = "plan_id")
    private Long planId;               // 所属招聘计划ID

    @Column(name = "plan_name")
    private String planName;           // 所属招聘计划名称

    @Column(name = "org_unit_name")
    private String orgUnitName;        // 所属单位名称

    @Column(name = "work_years")
    private Double workYears;          // 工作年限

    @Column(name = "expected_salary")
    private String expectedSalary;     // 期望薪资

    @Column(name = "available_date")
    private String availableDate;      // 到岗时间

    @Column(name = "emergency_contact")
    private String emergencyContact;   // 紧急联系人

    @Column(name = "emergency_phone")
    private String emergencyPhone;     // 紧急联系人电话

    @Column(name = "score_first_interview")
    private Double scoreFirstInterview;    // 初试分数

    @Column(name = "score_second_interview")
    private Double scoreSecondInterview;   // 复试分数

    @Column(name = "interviewer_first")
    private String interviewerFirst;       // 初试面试官

    @Column(name = "interviewer_second")
    private String interviewerSecond;      // 复试面试官

    @Column(name = "comment_first_interview")
    private String commentFirstInterview;  // 初试评语

    @Column(name = "comment_second_interview")
    private String commentSecondInterview; // 复试评语

    @Column(name = "screening_result")
    private String screeningResult;        // 初筛结果

    @Column(name = "interview_schedule")
    private String interviewSchedule;      // 面试排期时间

    @Column(name = "medical_result")
    private String medicalResult;          // 体检结果

    @Column(name = "background_check_result")
    private String backgroundCheckResult;  // 背调结果

    @Column(name = "hire_conclusion")
    private String hireConclusion;         // 录用结论

    @Column(name = "certificates")
    private String certificates;           // 证书资质

    @Column(name = "awards")
    private String awards;                 // 获奖经历

    @Column(name = "self_evaluation", columnDefinition = "TEXT")
    private String selfEvaluation;         // 自我评价

    // 工作经历
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<WorkExperience> workExperiences;

    // 项目经历
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<ProjectExperience> projectExperiences;
    
    // Getters and Setters
    public String getCandidateNo() { return candidateNo; }
    public void setCandidateNo(String candidateNo) { this.candidateNo = candidateNo; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getAppliedPositionId() { return appliedPositionId; }
    public void setAppliedPositionId(Long appliedPositionId) { this.appliedPositionId = appliedPositionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getResumeFilePath() { return resumeFilePath; }
    public void setResumeFilePath(String resumeFilePath) { this.resumeFilePath = resumeFilePath; }
    public Double getScoreWritten() { return scoreWritten; }
    public void setScoreWritten(Double scoreWritten) { this.scoreWritten = scoreWritten; }
    public Double getScoreInterview() { return scoreInterview; }
    public void setScoreInterview(Double scoreInterview) { this.scoreInterview = scoreInterview; }
    public Double getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(Double scoreTotal) { this.scoreTotal = scoreTotal; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    // New field getters/setters
    public String getResumeSource() { return resumeSource; }
    public void setResumeSource(String resumeSource) { this.resumeSource = resumeSource; }
    
    public String getCommentWritten() { return commentWritten; }
    public void setCommentWritten(String commentWritten) { this.commentWritten = commentWritten; }
    
    public String getInterviewer() { return interviewer; }
    public void setInterviewer(String interviewer) { this.interviewer = interviewer; }
    
    public String getCommentInterview() { return commentInterview; }
    public void setCommentInterview(String commentInterview) { this.commentInterview = commentInterview; }
    
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    
    public List<WorkExperience> getWorkExperiences() { return workExperiences; }
    public void setWorkExperiences(List<WorkExperience> workExperiences) { this.workExperiences = workExperiences; }
    
    public List<ProjectExperience> getProjectExperiences() { return projectExperiences; }
    public void setProjectExperiences(List<ProjectExperience> projectExperiences) { this.projectExperiences = projectExperiences; }
    
    public String getAppliedPosition() { return appliedPosition; }
    public void setAppliedPosition(String appliedPosition) { this.appliedPosition = appliedPosition; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getDomicile() { return domicile; }
    public void setDomicile(String domicile) { this.domicile = domicile; }

    public String getCurrentAddress() { return currentAddress; }
    public void setCurrentAddress(String currentAddress) { this.currentAddress = currentAddress; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getOrgUnitName() { return orgUnitName; }
    public void setOrgUnitName(String orgUnitName) { this.orgUnitName = orgUnitName; }

    public Double getWorkYears() { return workYears; }
    public void setWorkYears(Double workYears) { this.workYears = workYears; }

    public String getExpectedSalary() { return expectedSalary; }
    public void setExpectedSalary(String expectedSalary) { this.expectedSalary = expectedSalary; }

    public String getAvailableDate() { return availableDate; }
    public void setAvailableDate(String availableDate) { this.availableDate = availableDate; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public Double getScoreFirstInterview() { return scoreFirstInterview; }
    public void setScoreFirstInterview(Double scoreFirstInterview) { this.scoreFirstInterview = scoreFirstInterview; }

    public Double getScoreSecondInterview() { return scoreSecondInterview; }
    public void setScoreSecondInterview(Double scoreSecondInterview) { this.scoreSecondInterview = scoreSecondInterview; }

    public String getInterviewerFirst() { return interviewerFirst; }
    public void setInterviewerFirst(String interviewerFirst) { this.interviewerFirst = interviewerFirst; }

    public String getInterviewerSecond() { return interviewerSecond; }
    public void setInterviewerSecond(String interviewerSecond) { this.interviewerSecond = interviewerSecond; }

    public String getCommentFirstInterview() { return commentFirstInterview; }
    public void setCommentFirstInterview(String commentFirstInterview) { this.commentFirstInterview = commentFirstInterview; }

    public String getCommentSecondInterview() { return commentSecondInterview; }
    public void setCommentSecondInterview(String commentSecondInterview) { this.commentSecondInterview = commentSecondInterview; }

    public String getScreeningResult() { return screeningResult; }
    public void setScreeningResult(String screeningResult) { this.screeningResult = screeningResult; }

    public String getInterviewSchedule() { return interviewSchedule; }
    public void setInterviewSchedule(String interviewSchedule) { this.interviewSchedule = interviewSchedule; }

    public String getMedicalResult() { return medicalResult; }
    public void setMedicalResult(String medicalResult) { this.medicalResult = medicalResult; }

    public String getBackgroundCheckResult() { return backgroundCheckResult; }
    public void setBackgroundCheckResult(String backgroundCheckResult) { this.backgroundCheckResult = backgroundCheckResult; }

    public String getHireConclusion() { return hireConclusion; }
    public void setHireConclusion(String hireConclusion) { this.hireConclusion = hireConclusion; }

    public String getCertificates() { return certificates; }
    public void setCertificates(String certificates) { this.certificates = certificates; }

    public String getAwards() { return awards; }
    public void setAwards(String awards) { this.awards = awards; }

    public String getSelfEvaluation() { return selfEvaluation; }
    public void setSelfEvaluation(String selfEvaluation) { this.selfEvaluation = selfEvaluation; }
}
