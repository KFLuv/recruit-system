package com.recruitment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class FeishuMessageService {

    @Value("${feishu.app.id:}")
    private String appKey;

    @Value("${feishu.app.secret:}")
    private String appSecret;

    @Value("${feishu.webhook.url:}")
    private String webhookUrl;

    @Value("${feishu.send.enabled:true}")
    private boolean enabled;

    @Value("${feishu.default.receive.user:admin}")
    private String defaultReceiveUser;

    private String accessToken;

    @PostConstruct
    public void init() {
        System.out.println("飞书消息服务初始化");
    }

    /**
     * 获取访问令牌
     */
    private String getAccessToken() {
        if (accessToken != null) {
            return accessToken;
        }

        if (!enabled || appKey.isEmpty() || appSecret.isEmpty() || webhookUrl.isEmpty()) {
            return null;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> params = new HashMap<>();
            params.put("app_key", appKey);
            params.put("app_secret", appSecret);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal",
                request,
                String.class
            );

            if (response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                String body = response.getBody();
                
                // 简单解析 JSON
                String tenantAccessToken = extractJsonValue(body, "tenant_access_token");
                if (tenantAccessToken != null) {
                    accessToken = tenantAccessToken;
                    return accessToken;
                }
            }
        } catch (Exception e) {
            System.err.println("获取飞书访问令牌失败：" + e.getMessage());
        }

        return null;
    }

    /**
     * 发送文本消息
     */
    public boolean sendMessage(String userId, String content) {
        if (!enabled || userId == null || content == null || content.isEmpty()) {
            System.err.println("消息发送失败：未启用或参数为空");
            return false;
        }

        System.out.println("【飞书消息发送】");
        System.out.println("  Webhook 配置：" + (webhookUrl != null && !webhookUrl.isEmpty() ? "已配置" : "未配置"));
        System.out.println("  AppKey 配置：" + (appKey != null && !appKey.isEmpty() ? "已配置" : "未配置"));
        System.out.println("  接收人：" + userId);
        System.out.println("  内容：" + content);

        // 如果配置了 Webhook，优先使用 Webhook（最简单）
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            System.out.println("  发送方式：Webhook");
            return sendViaWebhook(content);
        }

        // 否则使用 AccessToken
        String token = getAccessToken();
        if (token == null) {
            System.err.println("飞书消息发送失败：未配置或获取令牌失败");
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> message = new HashMap<>();
            message.put("chat_id", userId);
            message.put("msg_type", "text");

            Map<String, String> text = new HashMap<>();
            text.put("content", content);
            message.put("text", text);

            Map<String, Object> params = new HashMap<>();
            params.put("access_token", token);
            params.put("message", message);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://open.feishu.cn/open-apis/message/v4/send",
                request,
                String.class
            );

            if (response.getBody() != null) {
                String body = response.getBody();
                String code = extractJsonValue(body, "code");
                return "0".equals(code) || code == null;
            }
        } catch (Exception e) {
            System.err.println("发送飞书消息失败：" + e.getMessage());
        }

        return false;
    }

    /**
     * 通过 Webhook 发送消息（最简单的方式）
     */
    private boolean sendViaWebhook(String content) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 飞书官方机器人 Webhook 格式
            Map<String, String> text = new HashMap<>();
            text.put("text", content);
            
            Map<String, Object> params = new HashMap<>();
            params.put("msg_type", "text");
            params.put("content", text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);

            if (response.getBody() != null) {
                String body = response.getBody();
                String msg = extractJsonValue(body, "msg");
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    // 飞书成功时返回 {"code":0,...}，code 是数字 0 而非字符串，
                    // 手写解析器提取不到数字，这里直接判断响应体
                    if (body.contains("\"code\":0")) {
                        return true;
                    } else {
                        System.err.println("飞书返回错误：" + msg);
                    }
                } else {
                    System.err.println("HTTP 错误：" + response.getStatusCode());
                    System.err.println("响应内容：" + body);
                }
            }
        } catch (Exception e) {
            System.err.println("发送飞书 Webhook 消息失败：" + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 发送审批通知
     */
    public boolean sendApprovalNotice(String userId, String planName, String applicant, String action) {
        if (!enabled) {
            return false;
        }

        String content = String.format(
            "【招聘系统审批通知】\n\n" +
            "📋 计划名称：%s\n" +
            "👤 申请人：%s\n" +
            "⚡ 操作：%s\n\n" +
            "请及时处理。",
            planName, applicant, action
        );

        return sendMessage(userId, content);
    }

    /**
     * 发送拟录用通知
     */
    public boolean sendOfferNotice(String userId, String candidateName, String position) {
        if (!enabled) {
            return false;
        }

        String content = String.format(
            "【候选人拟录用通知】\n\n" +
            "👤 候选人：%s\n" +
            "🎯 应聘岗位：%s\n\n" +
            "请及时确认录用意向。",
            candidateName, position
        );

        return sendMessage(userId, content);
    }

    /**
     * 从 JSON 中提取字符串值
     */
    private String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) return null;

            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) return null;

            int valueStart = json.indexOf("\"", colonIndex);
            if (valueStart == -1) return null;

            int valueEnd = json.indexOf("\"", valueStart + 1);
            if (valueEnd == -1) return null;

            return json.substring(valueStart + 1, valueEnd);
        } catch (Exception e) {
            return null;
        }
    }
}
