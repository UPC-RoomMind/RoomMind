package com.example.web.controller;

import com.example.web.dto.ChatInput;
import com.example.web.dto.ChatMessageDto;
import com.example.web.service.DeepSeekService;
import com.example.web.tools.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/AI")
public class AIController {

    @Autowired
    private DeepSeekService _DeepSeekService;

    @RequestMapping(value = "/Chat", method = RequestMethod.POST)
    public ResponseData<ChatMessageDto> Chat(@RequestBody ChatInput input) {
        if (input == null || input.getMessage() == null || input.getMessage().trim().isEmpty()) {
            return ResponseData.GetResponseDataInstance(null, "消息内容不能为空", false);
        }

        ChatMessageDto response = _DeepSeekService.chat(input);

        if (response.getContent() != null && response.getContent().startsWith("抱歉，AI服务暂时不可用")) {
            return ResponseData.GetResponseDataInstance(response, "AI服务调用失败", false);
        }

        return ResponseData.GetResponseDataInstance(response, "获取成功", true);
    }

    @RequestMapping(value = "/GetSystemPrompt", method = RequestMethod.POST)
    public ResponseData<String> GetSystemPrompt() {
        String prompt = _DeepSeekService.getSystemPrompt();
        return ResponseData.GetResponseDataInstance(prompt, "获取成功", true);
    }
}