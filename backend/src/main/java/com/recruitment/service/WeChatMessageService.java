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
public class WeChatMessageService {

    @Value("${wechat.corp.id:}")
    private String corpId;

    @Value("${wechat.agent.id:}")
    private String agentId;

    @Value("${wechat.secret:}")
    private String secret;

    @Value("${wechat.access.token.url:}")
    private String tokenUrl;

    @Value("${wechat.send.message.url:}")
    private String sendUrl;

    @Value("${wechat.send.enabled:true}")
    private boolean enabled;

    private String accessToken;
    private long tokenExpiryTime;

    @PostConstruct
    public void init() {
        // 初始化时检查配置
        if (!enabled) {
            System.out.println("企业微信消息发送已禁用");
        }
    }

    /**
     * 获取访问令牌
     */
    private String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return accessToken;
        }

        if (!enabled || corpId.isEmpty() || secret.isEmpty()) {
            return null;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> params = new HashMap<>();
            params.put("grant_type", "client_credential");
            params.put("appid", corpId);
            params.put("secret", secret);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.getForEntity(tokenUrl + "?" + toQueryString(params), String.class);

            if (response.getBody() != null) {
                Map<String, Object> result = new HashMap<>();
                // 简单解析 JSON
                String body = response.getBody();
                accessToken = extractJsonValue(body, "access_token");
                int expiresIn = extractIntValue(body, "expires_in");

                if (accessToken != null && expiresIn > 0) {
                    tokenExpiryTime = System.currentTimeMillis() + (expiresIn - 300) * 1000; // 提前 5 分钟过期
                    return accessToken;
                }
            }
        } catch (Exception e) {
            System.err.println("获取企业微信访问令牌失败：" + e.getMessage());
        }

        return null;
    }

    /**
     * 发送文本消息
     */
    public boolean sendMessage(String userId, String content) {
        if (!enabled || userId == null || content == null || content.isEmpty()) {
            return false;
        }

        // 模拟模式 - 打印日志而非真正发送
        if (corpId.isEmpty() || secret.isEmpty()) {
            System.out.println("【企业微信消息（模拟模式）】");
            System.out.println("  接收人：" + userId);
            System.out.println("  内容：" + content);
            System.out.println("  状态：已保存至日志（未实际发送）");
            System.out.println();
            return true;
        }

        String token = getAccessToken();
        if (token == null) {
            System.err.println("企业微信消息发送失败：未配置或获取令牌失败");
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> message = new HashMap<>();
            message.put("touser", userId);
            message.put("msgtype", "text");

            Map<String, String> text = new HashMap<>();
            text.put("content", content);
            message.put("text", text);

            Map<String, Object> params = new HashMap<>();
            params.put("access_token", token);
            params.put("msg", message);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(sendUrl + "?access_token=" + token, request, String.class);

            if (response.getBody() != null) {
                String body = response.getBody();
                int errCode = extractIntValue(body, "errcode");
                return errCode == 0 || errCode == 10000; // 0 表示成功，10000 表示用户不存在但消息已发送
            }
        } catch (Exception e) {
            System.err.println("发送企业微信消息失败：" + e.getMessage());
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
            "请点击企业微信中的链接进行处理。",
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
     * 发送预算预警
     */
    public boolean sendBudgetAlert(String userId, String planName, double budget, double used, double percentage) {
        if (!enabled) {
            return false;
        }

        String color = percentage >= 90 ? "🔴" : (percentage >= 80 ? "🟡" : "🟢");

        String content = String.format(
            "%s【预算预警提醒】\n\n" +
            "📋 计划名称：%s\n" +
            "💰 总预算：%.2f 万元\n" +
            "📊 已使用：%.2f 万元 (%.1f%%)\n\n" +
            "请注意控制招聘预算。",
            color, planName, budget, used, percentage
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

    /**
     * 从 JSON 中提取整数值
     */
    private int extractIntValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) return 0;

            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) return 0;

            int valueStart = colonIndex + 1;
            while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
                valueStart++;
            }

            int valueEnd = valueStart;
            while (valueEnd < json.length() && Character.isDigit(json.charAt(valueEnd))) {
                valueEnd++;
            }

            if (valueEnd > valueStart) {
                return Integer.parseInt(json.substring(valueStart, valueEnd));
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return 0;
    }

    /**
     * 将参数转换为查询字符串
     */
    private String toQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) sb.append("&");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
}
