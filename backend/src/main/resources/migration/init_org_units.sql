-- 创建部门表
CREATE TABLE IF NOT EXISTS org_unit (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT NULL COMMENT '父级部门 ID',
    org_name VARCHAR(200) NOT NULL COMMENT '部门名称',
    org_code VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
    org_type VARCHAR(50) DEFAULT NULL COMMENT '部门类型',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 1 正常 0 禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_org_code (org_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织架构部门表';

-- 初始化基础部门数据
INSERT INTO org_unit (id, org_name, org_code, org_type, level, sort_order, status) VALUES
(1, '总经办', 'DEPT001', 'management', 1, 1, 1),
(2, '人事部', 'DEPT002', 'hr', 2, 1, 1),
(3, '人力资源部', 'DEPT003', 'hr', 2, 2, 1),
(4, '财务部', 'DEPT004', 'finance', 2, 3, 1),
(5, '企划部', 'DEPT005', 'planning', 2, 4, 1),
(6, '技术研发部', 'DEPT006', 'tech', 2, 5, 1),
(7, '市场部', 'DEPT007', 'marketing', 2, 6, 1),
(8, '销售部', 'DEPT008', 'sales', 2, 7, 1);

-- 为 recruit_plan 表添加部门相关新字段
ALTER TABLE recruit_plan 
ADD COLUMN IF NOT EXISTS org_unit_name VARCHAR(200) DEFAULT NULL COMMENT '所属部门名称',
ADD COLUMN IF NOT EXISTS plan_owner VARCHAR(100) DEFAULT NULL COMMENT '计划负责人',
ADD COLUMN IF NOT EXISTS total_budget DECIMAL(10,2) DEFAULT NULL COMMENT '招聘总预算（万元）',
ADD COLUMN IF NOT EXISTS start_date DATE DEFAULT NULL COMMENT '招聘开始日期',
ADD COLUMN IF NOT EXISTS end_date DATE DEFAULT NULL COMMENT '招聘结束日期',
ADD COLUMN IF NOT EXISTS channels VARCHAR(500) DEFAULT NULL COMMENT '招聘渠道',
ADD COLUMN IF NOT EXISTS other_requirements TEXT DEFAULT NULL COMMENT '其他补充要求';

-- 更新现有数据的 status 字段
UPDATE recruit_plan SET status = 'draft' WHERE status IS NULL;
