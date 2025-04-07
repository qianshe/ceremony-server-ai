package com.qianshe.ceremonyserverai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String role; // 可以是 system, user, assistant, tool
    private String content;
    private List<ToolCall> toolCalls;
    private String toolCallId; // 仅在role="tool"时使用
} 