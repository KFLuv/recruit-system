package com.recruitment.dto;

import lombok.Data;

@Data
public class ProjectExperienceDTO {
    private Long id;
    private Long candidateId;
    private String projectName;
    private String projectStart;
    private String projectEnd;
    private String role;
    private String description;
    private String achievement;
    private String attachmentPath;
}
