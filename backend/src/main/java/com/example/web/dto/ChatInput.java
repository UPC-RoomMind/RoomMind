package com.example.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ChatInput {

    @JsonProperty("Message")
    private String Message;

    @JsonProperty("History")
    private List<ChatMessageDto> History;

    @JsonProperty("MaxTokens")
    private Integer MaxTokens;

    @JsonProperty("Temperature")
    private Double Temperature;

    @JsonProperty("UserId")
    private Integer UserId;
}