package com.qianshe.ceremonyserverai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolCall {
    private String id;
    private String name;
    private Map<String, Object> parameters;
} 