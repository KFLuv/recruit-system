-- 修复编制预算表预算字段大小限制
-- 将 DECIMAL(12,2) 改为 DECIMAL(20,2)，支持更大的预算值

ALTER TABLE annual_budget_config 
MODIFY COLUMN budget_limit DECIMAL(20,2) DEFAULT NULL COMMENT '预算上限（万元）';
