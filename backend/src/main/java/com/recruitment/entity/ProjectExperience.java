package com.recruitment.entity;

import javax.persistence.*;

@Entity
@Table(name = "project_experience")
public class ProjectExperience extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_start")
    private String projectStart;

    @Column(name = "project_end")
    private String projectEnd;

    @Column(name = "role")
    private String role;

    @Column(name = "description")
    private String description;

    @Column(name = "achievement")
    private String achievement;
    
    @Column(name = "attachment_path")
    private String attachmentPath;

    @ManyToOne
    @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
    private Candidate candidate;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectStart() { return projectStart; }
    public void setProjectStart(String projectStart) { this.projectStart = projectStart; }
    public String getProjectEnd() { return projectEnd; }
    public void setProjectEnd(String projectEnd) { this.projectEnd = projectEnd; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAchievement() { return achievement; }
    public void setAchievement(String achievement) { this.achievement = achievement; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
}
