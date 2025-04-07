package com.qianshe.ceremonyserverai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ChatConfig {

    final OllamaChatModel model;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(model).build();
    }
}
