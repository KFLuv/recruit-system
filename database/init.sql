-- 创建数据库（已在 docker-compose.yml 中创建，此处为兼容性）
-- CREATE DATABASE IF NOT EXISTS recruitment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE recruitment_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin 或 user',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 招聘计划表
CREATE TABLE IF NOT EXISTS `recruitment_plan` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `plan_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '计划编号',
    `unit_name` VARCHAR(100) NOT NULL COMMENT '申请单位',
    `position_name` VARCHAR(100) NOT NULL COMMENT '招聘岗位',
    `headcount` INT NOT NULL DEFAULT 0 COMMENT '招聘人数',
    `budget` DECIMAL(10,2) COMMENT '预算金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态：draft/pending/approved/rejected',
    `description` TEXT COMMENT '岗位描述',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否软删除',
    `current_version` INT DEFAULT 1 COMMENT '当前版本号',
    `creator_id` BIGINT COMMENT '创建人 ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan_no (`plan_no`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘计划表';

-- 计划版本表
CREATE TABLE IF NOT EXISTS `recruitment_plan_version` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `plan_id` BIGINT NOT NULL COMMENT '计划 ID',
    `plan_no` VARCHAR(50) NOT NULL COMMENT '计划编号',
    `version` INT NOT NULL COMMENT '版本号',
    `unit_name` VARCHAR(100) NOT NULL,
    `position_name` VARCHAR(100) NOT NULL,
    `headcount` INT NOT NULL,
    `budget` DECIMAL(10,2),
    `description` TEXT,
    `operate_type` VARCHAR(20) COMMENT '操作类型：create/update',
    `operator_id` BIGINT COMMENT '操作人 ID',
    `operate_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_plan_id (`plan_id`),
    INDEX idx_version (`plan_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘计划版本表';

-- 审批表
CREATE TABLE IF NOT EXISTS `approval_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `plan_id` BIGINT NOT NULL COMMENT '计划 ID',
    `plan_no` VARCHAR(50) NOT NULL COMMENT '计划编号',
    `approval_level` INT NOT NULL COMMENT '审批层级：1-二级单位，2-集团 HR，3-集团领导',
    `approver_id` BIGINT COMMENT '审批人 ID',
    `approver_name` VARCHAR(100) COMMENT '审批人姓名',
    `opinion` TEXT COMMENT '审批意见',
    `status` VARCHAR(20) NOT NULL COMMENT '审批状态：pending/approved/rejected',
    `approval_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_plan_id (`plan_id`),
    INDEX idx_level (`approval_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- 候选人表
CREATE TABLE IF NOT EXISTS `candidate` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `candidate_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '候选人编号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` VARCHAR(10) COMMENT '性别',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `education` VARCHAR(50) COMMENT '学历',
    `major` VARCHAR(100) COMMENT '专业',
    `graduation_date` DATE COMMENT '毕业时间',
    `plan_id` BIGINT COMMENT '关联计划 ID',
    `position` VARCHAR(100) COMMENT '应聘岗位',
    `status` VARCHAR(20) NOT NULL DEFAULT 'initial' COMMENT '状态：initial/exam/interview/offer/rejected',
    `resume_url` VARCHAR(500) COMMENT '简历 URL',
    `score` DECIMAL(5,2) COMMENT '综合评分',
    `remarks` TEXT COMMENT '备注',
    `creator_id` BIGINT COMMENT '创建人 ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_candidate_no (`candidate_no`),
    INDEX idx_status (`status`),
    INDEX idx_plan_id (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='候选人表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `operator_id` BIGINT COMMENT '操作人 ID',
    `operator_name` VARCHAR(100) COMMENT '操作人姓名',
    `ip_address` VARCHAR(50) COMMENT 'IP 地址',
    `module` VARCHAR(50) COMMENT '操作模块',
    `operation` VARCHAR(100) COMMENT '操作描述',
    `business_id` BIGINT COMMENT '业务 ID',
    `business_type` VARCHAR(50) COMMENT '业务类型',
    `operate_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operator (`operator_id`),
    INDEX idx_time (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 文件归档表
CREATE TABLE IF NOT EXISTS `file_archive` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `file_name` VARCHAR(200) NOT NULL COMMENT '文件名',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件 URL',
    `file_type` VARCHAR(50) COMMENT '文件类型',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `related_type` VARCHAR(50) COMMENT '关联类型：plan/candidate',
    `related_id` BIGINT COMMENT '关联业务 ID',
    `uploader_id` BIGINT COMMENT '上传人 ID',
    `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_related (`related_type`, `related_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件归档表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS `system_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `log_level` VARCHAR(20) COMMENT '日志级别',
    `log_message` TEXT COMMENT '日志内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 飞书消息记录表
CREATE TABLE IF NOT EXISTS `feishu_message_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `message_type` VARCHAR(50) COMMENT '消息类型',
    `recipient` VARCHAR(100) COMMENT '接收人',
    `content` TEXT COMMENT '消息内容',
    `send_status` VARCHAR(20) COMMENT '发送状态：success/failed',
    `error_message` TEXT COMMENT '错误信息',
    `send_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_send_status (`send_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='飞书消息记录表';

-- 插入测试账号（密码 `<your-password>`，加密后）
INSERT INTO `user` (`username`, `password`, `role`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin'),
('readonly', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user');
