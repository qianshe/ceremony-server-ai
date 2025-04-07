package com.qianshe.ceremonyserverai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * 仪式服务AI应用主类
 * <p>
 * 使用Spring AI与本地Ollama模型通信，提供AI对话和嵌入向量生成能力
 * 集成MCP功能，让AI可以调用外部工具、数据库和API
 * </p>
 */
@SpringBootApplication
public class CeremonyServerAiApplication {

    private static final Logger logger = LoggerFactory.getLogger(CeremonyServerAiApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(CeremonyServerAiApplication.class, args);
        logger.info("仪式服务AI系统已启动");
        
        // 打印已注册的Bean数量，用于调试
        logger.debug("应用程序上下文包含 {} 个Bean", context.getBeanDefinitionCount());
    }
} 