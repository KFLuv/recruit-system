-- =========================================
-- v2.0 企业级改造：审批流字段补充
-- MySQL 5.7 兼容版本
-- =========================================

ALTER TABLE process_approval 
ADD COLUMN approval_nodes VARCHAR(500) DEFAULT NULL COMMENT '完整审批链路（逗号分隔）';
