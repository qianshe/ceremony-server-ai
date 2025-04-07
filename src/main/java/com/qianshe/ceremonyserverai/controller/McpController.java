package com.qianshe.ceremonyserverai.controller;

import com.qianshe.ceremonyserverai.model.Tool;
import com.qianshe.ceremonyserverai.model.ToolCall;
import com.qianshe.ceremonyserverai.model.ToolCallResult;
import com.qianshe.ceremonyserverai.service.McpService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * MCP控制器，仅供内部系统使用，不再对外暴露API端点
 */
@Component
public class McpController {

    private final McpService mcpService;

    @Autowired
    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }

    public List<Tool> getTools() {
        return mcpService.getAvailableTools();
    }

    public ToolCallResult executeToolCall(String id, String name, Map<String, Object> parameters) {
        ToolCall toolCall = new ToolCall();
        toolCall.setId(id);
        toolCall.setName(name);
        toolCall.setParameters(parameters);
        
        return mcpService.executeToolCall(toolCall);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolsResponse {
        private List<Tool> tools;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCallRequest {
        private String id;
        private String name;
        private Map<String, Object> parameters;
    }
} 