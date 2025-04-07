package com.qianshe.config;

import com.qianshe.tools.DateTimeTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MCPConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(DateTimeTools dateTimeTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(dateTimeTools)
                .build();
    }
}
