package com.recruitment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Bug 报告实体
 */
@Entity
@Table(name = "bug_reports")
public class BugReport extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false, length = 2000)
    private String description;

    // ===== Getters & Setters =====

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
