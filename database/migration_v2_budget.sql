-- =========================================
-- v2.0 企业级改造：编制预算配置表
-- =========================================

-- 年度编制预算配置表（集团管控的核心）
CREATE TABLE IF NOT EXISTS annual_budget_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置 ID',
    config_year INT NOT NULL COMMENT '配置年度',
    org_unit_id BIGINT NOT NULL COMMENT '单位 ID',
    headcount_limit INT NOT NULL COMMENT '编制人数上限',
    budget_limit DECIMAL(12,2) DEFAULT NULL COMMENT '预算上限（万元）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_year_org (config_year, org_unit_id),
    INDEX idx_year (config_year),
    FOREIGN KEY (org_unit_id) REFERENCES org_unit(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年度编制预算配置表';

-- 插入测试数据：各单位2026年编制预算
INSERT INTO annual_budget_config (config_year, org_unit_id, headcount_limit, budget_limit) VALUES
(2026, 1, 200, 5000.00),  -- 集团总部 200人编制
(2026, 2, 80, 2000.00),   -- 第一二级单位 80人
(2026, 3, 60, 1500.00)    -- 第二二级单位 60人
ON DUPLICATE KEY UPDATE headcount_limit = VALUES(headcount_limit);

-- =========================================
-- 计划版本管理表（字段级差异追踪）
-- =========================================
CREATE TABLE IF NOT EXISTS plan_version (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '版本 ID',
    plan_id BIGINT NOT NULL COMMENT '计划 ID',
    version_no INT NOT NULL COMMENT '版本号',
    changed_fields JSON COMMENT '变更的字段及前后值（JSON）',
    change_summary VARCHAR(500) DEFAULT NULL COMMENT '变更摘要',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人 ID',
    operator_name VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_plan (plan_id),
    FOREIGN KEY (plan_id) REFERENCES recruit_plan(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计划版本记录表';
