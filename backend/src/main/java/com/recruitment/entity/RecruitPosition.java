package com.recruitment.entity;

import javax.persistence.*;

@Entity
@Table(name = "recruit_position")
public class RecruitPosition extends BaseEntity {

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    private RecruitPlan plan;

    @Column(name = "position_name", nullable = false)
    private String positionName;

    @Column(name = "major_required")
    private String majorRequired;

    @Column(name = "position_category")
    private String positionCategory;

    @Column(name = "recruitment_count", nullable = false)
    private Integer recruitmentCount;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "qualification_req")
    private String qualificationReq;

    private String status = "active";

    @Column(name = "dept_name")
    private String deptName;             // 用人部门

    @Column(name = "`rank`")
    private String rank;                 // 岗位职级

    @Column(name = "work_location")
    private String workLocation;         // 工作地点

    @Column(name = "salary_range")
    private String salaryRange;          // 薪资范围

    @Column(name = "employment_type")
    private String employmentType;       // 用工类型

    @Column(name = "experience_req")
    private String experienceReq;        // 经验要求

    @Column(name = "age_min")
    private Integer ageMin;              // 年龄下限

    @Column(name = "age_max")
    private Integer ageMax;              // 年龄上限

    @Column(name = "political_req")
    private String politicalReq;         // 政治面貌要求

    @Column(name = "special_req")
    private String specialReq;           // 特殊限制

    @Column(name = "job_responsibility", columnDefinition = "TEXT")
    private String jobResponsibility;    // 岗位职责

    @Column(name = "job_requirement", columnDefinition = "TEXT")
    private String jobRequirement;       // 岗位需求（技术能力要求）

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public RecruitPlan getPlan() { return plan; }
    public void setPlan(RecruitPlan plan) { this.plan = plan; }
    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
    public String getMajorRequired() { return majorRequired; }
    public void setMajorRequired(String majorRequired) { this.majorRequired = majorRequired; }
    public String getPositionCategory() { return positionCategory; }
    public void setPositionCategory(String positionCategory) { this.positionCategory = positionCategory; }
    public Integer getRecruitmentCount() { return recruitmentCount; }
    public void setRecruitmentCount(Integer recruitmentCount) { this.recruitmentCount = recruitmentCount; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public String getQualificationReq() { return qualificationReq; }
    public void setQualificationReq(String qualificationReq) { this.qualificationReq = qualificationReq; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    public String getWorkLocation() { return workLocation; }
    public void setWorkLocation(String workLocation) { this.workLocation = workLocation; }
    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getExperienceReq() { return experienceReq; }
    public void setExperienceReq(String experienceReq) { this.experienceReq = experienceReq; }

    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }

    public Integer getAgeMax() { return ageMax; }
    public void setAgeMax(Integer ageMax) { this.ageMax = ageMax; }

    public String getPoliticalReq() { return politicalReq; }
    public void setPoliticalReq(String politicalReq) { this.politicalReq = politicalReq; }

    public String getSpecialReq() { return specialReq; }
    public void setSpecialReq(String specialReq) { this.specialReq = specialReq; }

    public String getJobResponsibility() { return jobResponsibility; }
    public void setJobResponsibility(String jobResponsibility) { this.jobResponsibility = jobResponsibility; }

    public String getJobRequirement() { return jobRequirement; }
    public void setJobRequirement(String jobRequirement) { this.jobRequirement = jobRequirement; }
}
