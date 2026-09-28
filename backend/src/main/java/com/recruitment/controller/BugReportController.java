package com.recruitment.controller;

import com.recruitment.service.BugReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Bug 报告接口
 *
 * URL: POST /api/bug-reports
 * 前端调用: app.js -> fetch(API + '/bug-reports', { method: 'POST', ... })
 */
@RestController
@RequestMapping("/api/bug-reports")
public class BugReportController {

    @Autowired
    private BugReportService bugReportService;

    /**
     * 提交 Bug 报告
     * 请求体: { "title": "xxx", "severity": "medium", "description": "xxx" }
     */
    @PostMapping
    public Map<String, Object> createBugReport(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            String title = body.get("title");
            String severity = body.get("severity");
            String description = body.get("description");

            if (title == null || title.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Bug 标题不能为空");
                return result;
            }
            if (description == null || description.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Bug 描述不能为空");
                return result;
            }

            bugReportService.createBugReport(title, severity, description);
            result.put("success", true);
            result.put("message", "Bug 报告提交成功，感谢您的反馈！");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "提交失败：" + e.getMessage());
        }
        return result;
    }
}
