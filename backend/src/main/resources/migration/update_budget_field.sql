-- 扩大预算字段长度以支持更大的数值
ALTER TABLE recruit_plan 
MODIFY COLUMN total_budget DECIMAL(18,2) DEFAULT NULL COMMENT '总预算';

ALTER TABLE annual_budget_config
MODIFY COLUMN budget_limit DECIMAL(18,2) DEFAULT NULL COMMENT '预算上限';
