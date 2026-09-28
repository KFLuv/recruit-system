package com.recruitment.service;

import com.recruitment.entity.BugReport;
import com.recruitment.repository.BugReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Bug 报告业务逻辑层
 */
@Service
@Transactional
public class BugReportService {

    @Autowired
    private BugReportRepository bugReportRepository;

    /**
     * 提交 Bug 报告
     */
    public BugReport createBugReport(String title, String severity, String description) {
        BugReport report = new BugReport();
        report.setTitle(title);
        report.setSeverity(severity);
        report.setDescription(description);
        return bugReportRepository.save(report);
    }

    /**
     * 查询所有 Bug 报告
     */
    public List<BugReport> getAllBugReports() {
        return bugReportRepository.findAll();
    }
}
