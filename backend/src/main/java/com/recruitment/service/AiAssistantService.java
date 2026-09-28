package com.recruitment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.dto.CandidateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AiAssistantService {

    @Value("${ai.api.url:http://localhost:8000/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.model.name:Qwen3.5-35B-A3B}")
    private String modelName;

    @Value("${ai.api.key:vllm}")
    private String apiKey;

    @Value("${ai.api.timeout:60000}")
    private int timeout;

    @Value("${ai.api.enable:true}")
    private boolean enabled;

    @Value("${ai.system.prompt:你是一个专业、智能的 HR 招聘助手。请提供专业、实用、简洁的回答。使用中文回复。}")
    private String systemPrompt;

    private OkHttpClient httpClient;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
        objectMapper = new ObjectMapper();
    }

    /**
     * 调用真实 AI API
     */
    public Map<String, Object> callAi(String prompt, String context) {
        if (!enabled) {
            return createFallbackResponse(prompt, context, "AI 服务已禁用");
        }

        try {
            String jsonBody = String.format(
                    "{\n" +
                    "  \"model\": \"%s\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"%s\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"%s\"}\n" +
                    "  ],\n" +
                    "  \"temperature\": 0.7,\n" +
                    "  \"max_tokens\": 2000\n" +
                    "}",
                    modelName,
                    escapeJson(systemPrompt),
                    escapeJson(prompt)
            );

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "未知错误";
                    throw new IOException("AI API 调用失败：" + response.code() + " - " + errorBody);
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                return parseResponse(responseBody, context);
            }
        } catch (Exception e) {
            return createFallbackResponse(prompt, context, e.getMessage());
        }
    }

    /**
     * 解析 AI 响应
     */
    private Map<String, Object> parseResponse(String responseBody, String context) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            if (jsonNode.has("choices") && jsonNode.get("choices").isArray() 
                    && jsonNode.get("choices").size() > 0) {
                
                JsonNode contentNode = jsonNode.get("choices").get(0).get("message").get("content");
                String content = contentNode != null ? contentNode.asText() : "AI 返回了空内容";
                
                result.put("success", true);
                result.put("content", content);
                result.put("context", context);
                result.put("rawResponse", responseBody);
                result.put("timestamp", new Date());
            } else {
                result.put("success", false);
                result.put("error", "AI 响应格式错误");
                result.put("rawResponse", responseBody);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "解析 AI 响应失败：" + e.getMessage());
            result.put("rawResponse", responseBody);
        }
        
        return result;
    }

    /**
     * 简历智能分析
     */
    public Map<String, Object> analyzeResume(String candidateName, CandidateDTO candidate) {
        // 构建简历文本
        StringBuilder resumeText = new StringBuilder();
        resumeText.append("候选人姓名：").append(candidate.getRealName()).append("\n");
        resumeText.append("性别：").append(candidate.getGender()).append("\n");
        resumeText.append("年龄：").append(candidate.getAge()).append("\n");
        resumeText.append("学历：").append(candidate.getEducation()).append("\n");
        resumeText.append("毕业院校：").append(candidate.getUniversity()).append("\n");
        resumeText.append("专业：").append(candidate.getMajor()).append("\n");
        resumeText.append("工作年限：").append(candidate.getWorkYears() != null ? candidate.getWorkYears() : 0).append("年\n");
        resumeText.append("应聘岗位：").append(candidate.getAppliedPosition()).append("\n");
        resumeText.append("简历来源：").append(candidate.getResumeSource()).append("\n");
        resumeText.append("联系电话：").append(candidate.getPhone()).append("\n");
        resumeText.append("电子邮箱：").append(candidate.getEmail()).append("\n");

        if (candidate.getWorkExperiences() != null && !candidate.getWorkExperiences().isEmpty()) {
            resumeText.append("\n工作经历：\n");
            for (int i = 0; i < candidate.getWorkExperiences().size(); i++) {
                var work = candidate.getWorkExperiences().get(i);
                resumeText.append((i + 1)).append(". ").append(work.getCompanyName())
                        .append(" - ").append(work.getPosition())
                        .append(" (").append(work.getStartDate()).append(" 至 ").append(work.getEndDate()).append(")\n");
                if (work.getDescription() != null) {
                    resumeText.append("   ").append(work.getDescription()).append("\n");
                }
            }
        }

        if (candidate.getProjectExperiences() != null && !candidate.getProjectExperiences().isEmpty()) {
            resumeText.append("\n项目经历：\n");
            for (int i = 0; i < candidate.getProjectExperiences().size(); i++) {
                var project = candidate.getProjectExperiences().get(i);
                resumeText.append((i + 1)).append(". ").append(project.getProjectName())
                        .append(" - ").append(project.getRole()).append("\n");
                if (project.getDescription() != null) {
                    resumeText.append("   ").append(project.getDescription()).append("\n");
                }
            }
        }

        String prompt = "你是一个专业的 HR 简历分析专家。请根据以下简历信息，生成一份详细的分析报告：\n\n" +
                resumeText.toString() +
                "\n\n请输出以下内容（使用 Markdown 格式）：\n" +
                "## 📊 简历分析报告\n\n" +
                "### 1. 候选人核心优势\n" +
                "- 列出 3-5 个主要优势\n\n" +
                "### 2. 岗位匹配度评估\n" +
                "- 给出 0-100 分的匹配度评分\n" +
                "- 说明评分理由\n\n" +
                "### 3. 主要优势标签\n" +
                "- 列出 5-8 个关键标签\n\n" +
                "### 4. 潜在风险点\n" +
                "- 如有，列出需要注意的地方\n\n" +
                "### 5. 面试建议重点\n" +
                "- 列出 3-5 个面试时需要重点关注的问题\n\n" +
                "### 6. 综合推荐意见\n" +
                "- 强烈推荐 / 推荐 / 谨慎考虑 / 不推荐\n\n" +
                "请用专业、简洁的中文输出。";

        return callAi(prompt, "简历智能分析");
    }

    /**
     * 生成招聘报表
     */
    public Map<String, Object> generateRecruitmentReport(String reportType, Map<String, Object> data) {
        StringBuilder dataText = new StringBuilder();
        data.forEach((key, value) -> dataText.append(key).append(": ").append(value).append("\n"));

        String prompt = "请根据以下招聘数据生成分析报告：\n\n" +
                "数据：\n" + dataText.toString() + "\n\n" +
                "报告类型：" + reportType + "\n\n" +
                "请输出以下内容：\n" +
                "1. 数据概览\n" +
                "2. 关键发现\n" +
                "3. 趋势分析\n" +
                "4. 改进建议\n\n" +
                "请用专业、简洁的数据分析语言，使用 Markdown 格式。";

        return callAi(prompt, "报告生成");
    }

    /**
     * 日常聊天
     */
    public Map<String, Object> dailyChat(String message) {
        String prompt = "你是一个友好、专业的 HR 助手。请用轻松友好的语气回答：\n\n" + message + "\n\n" +
                "要求：\n" +
                "1. 语气友好、专业\n" +
                "2. 适当使用表情符号\n" +
                "3. 保持回答简洁\n" +
                "4. 可以提供有用的建议\n\n" +
                "请用中文回答。";

        return callAi(prompt, "日常聊天");
    }

    /**
     * 生成面试问题
     */
    public Map<String, Object> generateInterviewQuestions(String candidateName, String education, 
                                                           String workYears, String appliedPosition) {
        String prompt = "请为以下候选人生成面试问题：\n\n" +
                "候选人信息：\n" +
                "姓名：" + candidateName + "\n" +
                "学历：" + education + "\n" +
                "工作经验：" + workYears + "年\n" +
                "应聘岗位：" + appliedPosition + "\n\n" +
                "请生成 10 个面试问题，包括：\n" +
                "1. 个人背景类（2 个问题）\n" +
                "2. 专业技能类（3 个问题）\n" +
                "3. 工作经历类（2 个问题）\n" +
                "4. 情景模拟类（2 个问题）\n" +
                "5. 职业动机类（1 个问题）\n\n" +
                "请用中文输出，问题要具体、可操作。使用 Markdown 格式。";

        return callAi(prompt, "面试问题生成");
    }

    /**
     * 招聘咨询问答
     */
    public Map<String, Object> askRecruitmentQuestion(String question, String context) {
        String prompt = "你是一个专业的 HR 招聘助手。请回答以下问题：\n\n" +
                "问题：" + question + "\n\n" +
                "背景信息：" + (context != null ? context : "无") + "\n\n" +
                "请给出专业、实用的回答，包括：\n" +
                "1. 核心建议\n" +
                "2. 具体操作步骤\n" +
                "3. 注意事项\n\n" +
                "请用中文回答。";

        return callAi(prompt, "招聘咨询");
    }

    /**
     * 降级响应 - 当 AI 不可用时提供默认回答
     */
    private Map<String, Object> createFallbackResponse(String prompt, String context, String errorMsg) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", errorMsg);
        result.put("context", context);
        result.put("suggestion", "AI 服务暂时不可用，请检查网络或稍后再试");
        result.put("timestamp", new Date());
        return result;
    }

    /**
     * JSON 转义
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
