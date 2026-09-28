package com.recruitment.repository;

import com.recruitment.entity.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Bug 报告数据访问层
 */
@Repository
public interface BugReportRepository extends JpaRepository<BugReport, Long> {
}
