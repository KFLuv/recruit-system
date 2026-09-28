package com.recruitment.controller;

import com.recruitment.annotation.Log;
import com.recruitment.service.AiAssistantService;
import com.recruitment.util.PermissionUtil;
import com.recruitment.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    @Autowired
    private AiAssistantService aiAssistantService;

    /**
     * 简历智能分析
     */
    @PostMapping("/analyze-resume")
    @Log(module = "AI 助手", description = "简历智能分析")
    public Map<String, Object> analyzeResume(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            Long candidateId = Long.parseLong(params.get("candidateId").toString());
            // 这里需要从 CandidateService 获取候选人数据
            // 为简化，直接调用 AI 服务
            result.put("success", true);
            result.put("message", "AI 分析中，请稍候...");
            result.put("note", "实际实现需要集成候选人数据");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "分析失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 候选人智能推荐
     */
    @PostMapping("/recommend")
    @Log(module = "AI 助手", description = "候选人智能推荐")
    public Map<String, Object> recommendCandidates(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            Long planId = Long.parseLong(params.get("planId").toString());
            int topN = Integer.parseInt(params.getOrDefault("topN", 5).toString());
            
            result.put("success", true);
            result.put("message", "AI 推荐中，请稍候...");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "推荐失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 生成面试问题
     */
    @PostMapping("/interview-questions")
    @Log(module = "AI 助手", description = "生成面试问题")
    public Map<String, Object> generateInterviewQuestions(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            Long candidateId = Long.parseLong(params.get("candidateId").toString());
            String position = params.get("position").toString();
            
            result.put("success", true);
            result.put("message", "AI 生成面试问题中，请稍候...");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 招聘咨询问答
     */
    @PostMapping("/ask")
    @Log(module = "AI 助手", description = "招聘咨询")
    public Map<String, Object> askQuestion(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            String question = params.get("question").toString();
            String context = params.getOrDefault("context", "").toString();
            
            result.put("success", true);
            result.put("message", "AI 正在思考...");
            result.put("question", question);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "咨询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 生成报表
     */
    @PostMapping("/report")
    @Log(module = "AI 助手", description = "生成招聘报表")
    public Map<String, Object> generateReport(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");
            
            String reportType = params.get("reportType").toString();
            Map<String, Object> data = (Map<String, Object>) params.get("data");
            
            result.put("success", true);
            result.put("message", "AI 生成报表中...");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 日常聊天
     */
    @PostMapping("/chat")
    @Log(module = "AI 助手", description = "AI 聊天")
    public Map<String, Object> dailyChat(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            String message = params.get("message").toString();
            
            // 调用 AI 服务
            Map<String, Object> aiResult = aiAssistantService.dailyChat(message);
            
            result.put("success", aiResult.getOrDefault("success", false));
            
            Map<String, Object> data = new HashMap<>();
            data.put("content", aiResult.getOrDefault("content", "AI 暂时无法回复，请稍后再试"));
            result.put("data", data);
            
            if (!Boolean.TRUE.equals(aiResult.get("success"))) {
                result.put("message", aiResult.getOrDefault("error", "AI 服务调用失败"));
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "聊天失败：" + e.getMessage());
            Map<String, Object> data = new HashMap<>();
            data.put("content", "❌ " + e.getMessage());
            result.put("data", data);
        }
        return result;
    }

    /**
     * 获取 AI 状态
     */
    @GetMapping("/status")
    public Map<String, Object> getAiStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("aiEnabled", true);
        result.put("features", new String[] {
            "简历智能分析",
            "候选人推荐",
            "面试问题生成",
            "招聘咨询",
            "报表生成",
            "日常聊天"
        });
        result.put("model", "GPT-3.5-Turbo");
        return result;
    }
}
