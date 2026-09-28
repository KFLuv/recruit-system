package com.recruitment.dto;

import lombok.Data;

@Data
public class RecruitPositionDTO {
    private Long id;
    private Long planId;
    private String positionName;
    private String majorRequired;
    private String positionCategory;
    private Integer recruitmentCount;
    private String educationLevel;
    private String qualificationReq;
    private String status;
    private String deptName;
    private String rank;
    private String workLocation;
    private String salaryRange;
    private String employmentType;
    private String jobResponsibility;
    private String jobRequirement;
}
