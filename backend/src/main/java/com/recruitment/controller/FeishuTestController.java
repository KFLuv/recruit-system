package com.recruitment.controller;

import com.recruitment.annotation.Log;
import com.recruitment.service.FeishuMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class FeishuTestController {

    @Autowired
    private FeishuMessageService feishuMessageService;

    /**
     * 测试发送审批通知
     */
    @PostMapping("/send-approval")
    @Log(module = "飞书", description = "发送审批通知")
    public Map<String, Object> testApprovalNotice(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String userId = (String) params.getOrDefault("userId", "admin");
            String planName = (String) params.getOrDefault("planName", "2026 年度招聘计划");
            String applicant = (String) params.getOrDefault("applicant", "张三");

            boolean success = feishuMessageService.sendApprovalNotice(
                userId,
                planName,
                applicant,
                "测试审批通知"
            );

            result.put("success", success);
            result.put("message", success ? "消息已发送" : "发送失败");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 测试发送拟录用通知
     */
    @PostMapping("/send-offer")
    @Log(module = "飞书", description = "发送拟录用通知")
    public Map<String, Object> testOfferNotice(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String userId = (String) params.getOrDefault("userId", "admin");
            String candidateName = (String) params.getOrDefault("candidateName", "张三");
            String position = (String) params.getOrDefault("position", "Java 开发工程师");

            boolean success = feishuMessageService.sendOfferNotice(
                userId,
                candidateName,
                position
            );

            result.put("success", success);
            result.put("message", success ? "消息已发送" : "发送失败");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 测试发送普通文本消息
     */
    @PostMapping("/send-test")
    @Log(module = "飞书", description = "发送测试消息")
    public Map<String, Object> testSendMessage(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String content = (String) params.getOrDefault("content", "测试消息");

            boolean success = feishuMessageService.sendMessage("admin", content);

            result.put("success", success);
            result.put("message", success ? "消息已发送" : "发送失败");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试失败：" + e.getMessage());
        }
        return result;
    }
}
