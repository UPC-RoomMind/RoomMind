package com.example.web.service.impl;

import com.example.web.dto.ChatInput;
import com.example.web.dto.ChatMessageDto;
import com.example.web.service.AiContextService;
import com.example.web.service.DeepSeekService;
import com.example.web.tools.Extension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AiContextService aiContextService;

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

            String userMessage = input.getMessage() != null ? input.getMessage().trim() : "";

            // ========== 2. RAG：基于用户问题从系统查询上下文数据 ==========
            String contextData;
            try {
                contextData = aiContextService.buildContext(userMessage, input.getUserId());
            } catch (Exception ctxEx) {
                contextData = "【系统数据检索暂不可用】\n错误：" + ctxEx.getMessage() + "\n请基于常规系统规则回答。\n\n";
            }

            // 3. 构建动态 system prompt：固定角色 + 系统上下文数据
            StringBuilder dynamicPrompt = new StringBuilder();
            dynamicPrompt.append("你是一个【志高自习室预约系统】的智能助手，负责回答用户关于系统使用、预约规则、自习室介绍等问题。\n\n");
            dynamicPrompt.append("【角色定位】\n");
            dynamicPrompt.append("- 用简洁、清晰、友好的中文回答。\n");
            dynamicPrompt.append("- 回答必须严格基于下方【系统数据】，不要编造不存在的自习室、座位、用户数据或规则。\n");
            dynamicPrompt.append("- 如果系统数据中没有明确信息，可以基于系统规则给出建议，但要标注【这是建议，具体以系统实际数据为准】。\n\n");
            dynamicPrompt.append(contextData);
            dynamicPrompt.append("\n\n【用户身份】\n");
            dynamicPrompt.append(input.getUserId() != null ? "已登录用户（UserId: " + input.getUserId() + "），可以查询其个人预约和积分数据。\n" : "未登录用户，无法查询个人预约记录和积分，需要时请提示登录。\n");
            dynamicPrompt.append("\n【输出格式】\n");
            dynamicPrompt.append("- 关键信息（如自习室名称、步骤）用**加粗**标记。\n");
            dynamicPrompt.append("- 步骤型答案用 1. 2. 3. 列表。\n");
            dynamicPrompt.append("- 数字类答案（如积分、数量）要明确给出数值。\n");
            dynamicPrompt.append("- 禁止输出 markdown 代码块，直接用纯文本回答。\n");

            // 4. 构建请求体（使用 Jackson，字段名小写以符合 DeepSeek API 规范）
            ObjectNode bodyNode = objectMapper.createObjectNode();
            bodyNode.put("model", model);
            bodyNode.put("stream", false);

            Double temperature = input.getTemperature() != null ? input.getTemperature() : defaultTemperature;
            Integer maxTokens = input.getMaxTokens() != null ? input.getMaxTokens() : defaultMaxTokens;
            bodyNode.put("temperature", temperature);
            bodyNode.put("max_tokens", maxTokens);

            ArrayNode messagesNode = bodyNode.putArray("messages");

            // system message - 动态上下文
            ObjectNode systemMsg = messagesNode.addObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", dynamicPrompt.toString());

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
            userMsg.put("content", userMessage);

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