-- 重建 recruit_plan 表以匹配实体类结构
DROP TABLE IF EXISTS recruit_plan;

CREATE TABLE recruit_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划 ID',
    plan_year INT DEFAULT NULL COMMENT '招聘年份',
    plan_name VARCHAR(200) NOT NULL COMMENT '计划名称',
    org_unit_id BIGINT DEFAULT NULL COMMENT '所属单位 ID',
    org_unit_name VARCHAR(100) DEFAULT NULL COMMENT '所属单位名称',
    plan_owner VARCHAR(50) DEFAULT NULL COMMENT '计划负责人',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft 草稿，submitting 待审批，approved 已审批，rejected 已驳回，revoked 已作废',
    total_budget DECIMAL(12,2) DEFAULT NULL COMMENT '总预算',
    total_positions INT DEFAULT NULL COMMENT '总岗位数',
    total_candidates INT DEFAULT NULL COMMENT '总招聘人数',
    start_date DATE DEFAULT NULL COMMENT '开始日期',
    end_date DATE DEFAULT NULL COMMENT '结束日期',
    channels VARCHAR(255) DEFAULT NULL COMMENT '招聘渠道',
    other_requirements TEXT DEFAULT NULL COMMENT '其他补充要求',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    approval_time DATETIME DEFAULT NULL COMMENT '审批时间',
    approval_comment VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    creator_id BIGINT DEFAULT NULL COMMENT '创建人 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_plan_year (plan_year),
    INDEX idx_org_unit_id (org_unit_id),
    INDEX idx_status (status),
    INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘计划表';

-- 添加测试数据（如果存在的话）
INSERT INTO recruit_plan (plan_year, plan_name, org_unit_id, org_unit_name, plan_owner, status, total_budget, total_positions, total_candidates, start_date, end_date)
VALUES 
(2026, '2026 年集团招聘计划', 1, '集团总部', '张三', 'draft', 100.00, 3, 5, '2026-01-01', '2026-12-31')
ON DUPLICATE KEY UPDATE plan_name=VALUES(plan_name);
