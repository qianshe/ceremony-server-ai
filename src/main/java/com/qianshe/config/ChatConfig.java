package com.qianshe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ChatConfig {

    final OllamaChatModel model;

    @Bean
    public ChatClient chatClient(ChatMemory chatMemory) {
        return ChatClient.builder(model)
                // 设置系统消息
                .defaultSystem("你是我的专属客服小姐姐")
                //增强特性 PromptChatMemoryAdvisor,
                // MessageChatMemoryAdvisor
                // 用于在对话中添加记忆功能 可加入RAG，向量数据库查询能力
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }
    // ChatMemory 对话内存   用于保存对话历史记录，以便模型可以参考之前的对话内容
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

}
