package com.example.web.service;

import com.example.web.dto.ChatInput;
import com.example.web.dto.ChatMessageDto;

public interface DeepSeekService {

    ChatMessageDto chat(ChatInput input);

    String getSystemPrompt();
}