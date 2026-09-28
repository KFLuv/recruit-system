package com.recruitment.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 年度编制预算配置 —— 企业级管控核心表
 * 用于控制各单位每年的编制人数上限和预算上限
 */
@Entity
@Table(name = "annual_budget_config",
       uniqueConstraints = @UniqueConstraint(columnNames = {"config_year", "org_unit_id"}))
public class AnnualBudgetConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_year", nullable = false)
    private Integer configYear;

    @Column(name = "org_unit_id", nullable = false)
    private Long orgUnitId;

    @Column(name = "headcount_limit", nullable = false)
    private Integer headcountLimit;   // 编制人数上限

    @Column(name = "budget_limit")
    private BigDecimal budgetLimit;   // 预算上限（万元）

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getConfigYear() { return configYear; }
    public void setConfigYear(Integer configYear) { this.configYear = configYear; }

    public Long getOrgUnitId() { return orgUnitId; }
    public void setOrgUnitId(Long orgUnitId) { this.orgUnitId = orgUnitId; }

    public Integer getHeadcountLimit() { return headcountLimit; }
    public void setHeadcountLimit(Integer headcountLimit) { this.headcountLimit = headcountLimit; }

    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }
}
