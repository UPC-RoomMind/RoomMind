package com.example.web.service.impl;

import com.example.web.dto.ChatInput;
import com.example.web.dto.ChatMessageDto;
import com.example.web.service.DeepSeekService;
import com.example.web.tools.Extension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    @Value("${app.deepseek.api-key}")
    private String apiKey;

    @Value("${app.deepseek.base-url}")
    private String baseUrl;

    @Value("${app.deepseek.model}")
    private String model;

    @Value("${app.deepseek.temperature}")
    private Double defaultTemperature;

    @Value("${app.deepseek.max-tokens}")
    private Integer defaultMaxTokens;

    @Value("${app.deepseek.system-prompt}")
    private String systemPrompt;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ChatMessageDto chat(ChatInput input) {
        try {
            // 1. 检查 API key 是否配置
            if (apiKey == null || apiKey.trim().isEmpty() || apiKey.startsWith("your_")) {
                return new ChatMessageDto("assistant", 
                    "⚠️ AI助手尚未配置。请联系管理员在 application-dev.yml 中设置有效的 DeepSeek API Key。\n\n" +
                    "配置路径：app.deepseek.api-key");
            }

            // 2. 构建请求体（使用 Jackson，字段名小写以符合 DeepSeek API 规范）
            ObjectNode bodyNode = objectMapper.createObjectNode();
            bodyNode.put("model", model);
            bodyNode.put("stream", false);

            Double temperature = input.getTemperature() != null ? input.getTemperature() : defaultTemperature;
            Integer maxTokens = input.getMaxTokens() != null ? input.getMaxTokens() : defaultMaxTokens;
            bodyNode.put("temperature", temperature);
            bodyNode.put("max_tokens", maxTokens);

            ArrayNode messagesNode = bodyNode.putArray("messages");

            // system message
            ObjectNode systemMsg = messagesNode.addObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);

            // history messages
            if (input.getHistory() != null && !input.getHistory().isEmpty()) {
                for (ChatMessageDto msg : input.getHistory()) {
                    if (msg != null && Extension.isNotNullOrEmpty(msg.getContent())) {
                        ObjectNode histMsg = messagesNode.addObject();
                        histMsg.put("role", msg.getRole() != null ? msg.getRole().toLowerCase() : "user");
                        histMsg.put("content", msg.getContent());
                    }
                }
            }

            // user message
            ObjectNode userMsg = messagesNode.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", input.getMessage());

            String body = objectMapper.writeValueAsString(bodyNode);

            // 3. 发送请求
            URL url = new URL(baseUrl + "/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] inputBytes = body.getBytes(StandardCharsets.UTF_8);
                os.write(inputBytes, 0, inputBytes.length);
            }

            // 4. 读取响应
            int responseCode = connection.getResponseCode();
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String responseStr = response.toString();

            // 5. 使用 Jackson 解析响应
            JsonNode root = objectMapper.readTree(responseStr);

            // 检查是否有错误
            if (root.has("error")) {
                JsonNode errorNode = root.get("error");
                String errorMsg = errorNode.has("message") ? errorNode.get("message").asText() : responseStr;
                return new ChatMessageDto("assistant", 
                    "⚠️ AI服务调用失败：" + errorMsg + "\n\n" +
                    "请检查 API Key 是否正确，或稍后重试。");
            }

            // 提取 content
            JsonNode choicesNode = root.get("choices");
            if (choicesNode == null || !choicesNode.isArray() || choicesNode.size() == 0) {
                return new ChatMessageDto("assistant", "⚠️ 无法解析AI响应：" + responseStr);
            }

            JsonNode firstChoice = choicesNode.get(0);
            JsonNode messageNode = firstChoice.get("message");
            if (messageNode == null) {
                return new ChatMessageDto("assistant", "⚠️ AI响应格式错误：" + responseStr);
            }

            JsonNode contentNode = messageNode.get("content");
            String content = contentNode != null ? contentNode.asText() : "";

            if (content == null || content.trim().isEmpty()) {
                content = "（AI未返回内容）";
            }

            return new ChatMessageDto("assistant", content);

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatMessageDto("assistant", 
                "⚠️ AI服务暂时不可用。\n\n" +
                "错误信息：" + e.getMessage() + "\n\n" +
                "请稍后重试，或联系管理员检查服务配置。");
        }
    }

    @Override
    public String getSystemPrompt() {
        return systemPrompt;
    }
}