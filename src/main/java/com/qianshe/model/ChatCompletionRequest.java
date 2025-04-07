package com.qianshe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {
    private String model;
    private List<ChatMessage> messages;
    private List<Tool> tools;
    private boolean toolChoice;
    private Float temperature;
    private Integer maxTokens;
} 