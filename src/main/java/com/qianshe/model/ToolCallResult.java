package com.qianshe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallResult {
    private String toolCallId;
    private Object result;
} 