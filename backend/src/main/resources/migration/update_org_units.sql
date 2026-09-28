-- 更新部门表数据，修复层级关系
-- 集团总部：level=1
-- 二级单位：level=2
-- 三级单位：level=3

-- 清空现有部门数据
DELETE FROM org_unit;

-- 插入集团总部
INSERT INTO org_unit (id, org_name, org_code, org_type, level, sort_order, status) VALUES
(1, '集团总部', 'GROUP001', 'headquarters', 1, 1, 1),
(2, '集团人事部', 'GROUP002', 'hr', 2, 1, 1),
(3, '集团财务部', 'GROUP003', 'finance', 2, 2, 1),
(4, '集团领导', 'GROUP004', 'management', 2, 3, 1);

-- 插入二级单位
INSERT INTO org_unit (id, org_name, org_code, org_type, level, sort_order, status) VALUES
(5, '第一二级单位', 'UNIT001', 'secondary', 2, 1, 1),
(6, '第二二级单位', 'UNIT002', 'secondary', 2, 2, 1),
(7, '第三二级单位', 'UNIT003', 'secondary', 2, 3, 1);

-- 插入三级单位
INSERT INTO org_unit (id, org_name, org_code, org_type, level, sort_order, status) VALUES
(8, '第一二级单位 - 研发部', 'UNIT001-RD', 'tech', 3, 1, 1),
(9, '第一二级单位 - 销售部', 'UNIT001-SL', 'sales', 3, 2, 1),
(10, '第二二级单位 - 技术部', 'UNIT002-TS', 'tech', 3, 1, 1),
(11, '第二二级单位 - 市场部', 'UNIT002-MK', 'marketing', 3, 2, 1);

-- 更新编制预算校验配置表
-- 为集团总部配置编制预算
INSERT INTO annual_budget_config (id, config_year, org_unit_id, headcount_limit, budget_limit) 
VALUES (1, 2026, 1, 200, 5000)
ON DUPLICATE KEY UPDATE 
    headcount_limit = 200, 
    budget_limit = 5000;

-- 为第一二级单位配置编制预算
INSERT INTO annual_budget_config (id, config_year, org_unit_id, headcount_limit, budget_limit) 
VALUES (2, 2026, 5, 80, 2000)
ON DUPLICATE KEY UPDATE 
    headcount_limit = 80, 
    budget_limit = 2000;

-- 为第二二级单位配置编制预算
INSERT INTO annual_budget_config (id, config_year, org_unit_id, headcount_limit, budget_limit) 
VALUES (3, 2026, 6, 60, 1500)
ON DUPLICATE KEY UPDATE 
    headcount_limit = 60, 
    budget_limit = 1500;

-- 为集团 HR 配置审批链路
-- 二级单位（level=2）：二级单位审批 → 集团 HR 审批 → 集团领导审批
-- 集团总部（level=1）：集团 HR 审批 → 集团领导审批
