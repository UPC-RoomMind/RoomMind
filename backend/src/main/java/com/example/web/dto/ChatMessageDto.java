package com.example.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDto {

    @JsonProperty("Role")
    private String Role;

    @JsonProperty("Content")
    private String Content;

    @JsonProperty("Time")
    private LocalDateTime Time;

    public ChatMessageDto() {
        this.Time = LocalDateTime.now();
    }

    public ChatMessageDto(String role, String content) {
        this.Role = role;
        this.Content = content;
        this.Time = LocalDateTime.now();
    }
}