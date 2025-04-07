package com.qianshe.ceremonyserverai.config;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class OllamaConfig {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:deepseek-r1:8b}")
    private String model;

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi(baseUrl);
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        OllamaOptions options = OllamaOptions.builder()
                .withModel(model)
                .withTemperature(0.7f)
                .build();
        return new OllamaChatModel(ollamaApi, options);
    }

    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel(OllamaApi ollamaApi) {
        return new OllamaEmbeddingModel(ollamaApi, model);
    }
} 