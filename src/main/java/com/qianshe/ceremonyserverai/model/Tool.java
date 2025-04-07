package com.qianshe.ceremonyserverai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tool {
    private String name;
    private String description;
    private Map<String, Object> parameters;
} 