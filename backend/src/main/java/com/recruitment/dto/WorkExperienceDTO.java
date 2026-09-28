package com.recruitment.dto;

import lombok.Data;

@Data
public class WorkExperienceDTO {
    private Long id;
    private Long candidateId;
    private String companyName;
    private String position;
    private String startDate;
    private String endDate;
    private String description;
    private String attachmentPath;
}
