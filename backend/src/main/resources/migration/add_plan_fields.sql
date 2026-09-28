-- 为 recruit_plan 表添加新字段

ALTER TABLE recruit_plan 
ADD COLUMN org_unit_name VARCHAR(200) DEFAULT NULL COMMENT '所属单位名称',
ADD COLUMN plan_owner VARCHAR(100) DEFAULT NULL COMMENT '计划负责人',
ADD COLUMN total_budget DECIMAL(10,2) DEFAULT NULL COMMENT '招聘总预算（万元）',
ADD COLUMN start_date DATE DEFAULT NULL COMMENT '招聘开始日期',
ADD COLUMN end_date DATE DEFAULT NULL COMMENT '招聘结束日期',
ADD COLUMN channels VARCHAR(500) DEFAULT NULL COMMENT '招聘渠道',
ADD COLUMN other_requirements TEXT DEFAULT NULL COMMENT '其他补充要求';

-- 更新现有数据的 status 字段
UPDATE recruit_plan SET status = 'draft' WHERE status IS NULL;
