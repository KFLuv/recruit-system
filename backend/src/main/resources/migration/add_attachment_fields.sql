-- 工作经历添加附件路径字段
ALTER TABLE work_experience ADD COLUMN attachment_path VARCHAR(500) DEFAULT NULL COMMENT '附件路径';

-- 项目经历添加附件路径字段
ALTER TABLE project_experience ADD COLUMN attachment_path VARCHAR(500) DEFAULT NULL COMMENT '附件路径';
