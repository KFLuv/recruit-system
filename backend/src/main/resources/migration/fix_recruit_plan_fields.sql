-- 修复 recruit_plan 表的字段问题
-- 添加缺失的字段

-- 添加 plan_year 字段
ALTER TABLE recruit_plan ADD COLUMN plan_year INT DEFAULT NULL COMMENT '招聘年份';

-- 添加 plan_owner 字段
ALTER TABLE recruit_plan ADD COLUMN plan_owner VARCHAR(50) DEFAULT NULL COMMENT '计划负责人';

-- 添加 channels 字段
ALTER TABLE recruit_plan ADD COLUMN channels VARCHAR(255) DEFAULT NULL COMMENT '招聘渠道';

-- 添加 other_requirements 字段
ALTER TABLE recruit_plan ADD COLUMN other_requirements TEXT DEFAULT NULL COMMENT '其他补充要求';

-- 添加 creator_id 字段
ALTER TABLE recruit_plan ADD COLUMN creator_id BIGINT DEFAULT NULL COMMENT '创建人 ID';

-- 确保所有必要字段都存在
ALTER TABLE recruit_plan 
ADD COLUMN org_unit_id BIGINT DEFAULT NULL COMMENT '所属单位 ID',
ADD COLUMN org_unit_name VARCHAR(100) DEFAULT NULL COMMENT '所属单位名称';

-- 添加索引
CREATE INDEX idx_plan_year ON recruit_plan(plan_year);
CREATE INDEX idx_org_unit_id ON recruit_plan(org_unit_id);
CREATE INDEX idx_status ON recruit_plan(status);
