-- 为 candidate 表添加新字段
ALTER TABLE candidate 
ADD COLUMN resume_source VARCHAR(100) DEFAULT NULL COMMENT '简历来源',
ADD COLUMN comment_written TEXT DEFAULT NULL COMMENT '笔试评语',
ADD COLUMN interviewer VARCHAR(100) DEFAULT NULL COMMENT '面试官',
ADD COLUMN comment_interview TEXT DEFAULT NULL COMMENT '面试评语',
ADD COLUMN rating VARCHAR(10) DEFAULT NULL COMMENT '综合评级 A/B/C/D';

-- 创建工作经历表
CREATE TABLE IF NOT EXISTS work_experience (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    candidate_id BIGINT DEFAULT NULL COMMENT '候选人 ID',
    company_name VARCHAR(200) NOT NULL COMMENT '公司名称',
    position VARCHAR(100) NOT NULL COMMENT '岗位',
    start_date DATE DEFAULT NULL COMMENT '开始日期',
    end_date DATE DEFAULT NULL COMMENT '结束日期',
    description TEXT COMMENT '工作描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (candidate_id) REFERENCES candidate(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作经历表';

-- 创建项目经历表
CREATE TABLE IF NOT EXISTS project_experience (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    candidate_id BIGINT DEFAULT NULL COMMENT '候选人 ID',
    project_name VARCHAR(200) NOT NULL COMMENT '项目名称',
    project_time DATE DEFAULT NULL COMMENT '项目时间',
    role VARCHAR(100) NOT NULL COMMENT '本人角色',
    description TEXT COMMENT '项目详情',
    achievement TEXT COMMENT '业绩成果',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (candidate_id) REFERENCES candidate(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目经历表';

-- 添加候选人状态选项
UPDATE candidate SET status = 'new' WHERE status IS NULL;
