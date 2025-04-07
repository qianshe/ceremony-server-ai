package com.qianshe.ceremonyserverai.controller;

import com.qianshe.ceremonyserverai.entity.ChatHistory;
import com.qianshe.ceremonyserverai.model.ChatCompletionRequest;
import com.qianshe.ceremonyserverai.model.ChatCompletionResponse;
import com.qianshe.ceremonyserverai.service.AiService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @Autowired
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String response = aiService.chat(request.getPrompt());
        return new ChatResponse(response);
    }
    
    @PostMapping("/chat/completions")
    public ChatCompletionResponse chatCompletions(@RequestBody ChatCompletionRequest request) {
        return aiService.chatWithMcp(request);
    }

    @PostMapping("/embed")
    public EmbeddingResponse embed(@RequestBody EmbeddingRequest request) {
        List<Double> embedding = aiService.getEmbedding(request.getText());
        return new EmbeddingResponse(embedding);
    }
    
    @GetMapping("/history/recent")
    public List<ChatHistory> getRecentHistory() {
        return aiService.getRecentChatHistory();
    }
    
    @GetMapping("/history/search")
    public List<ChatHistory> searchHistory(@RequestParam String keyword) {
        return aiService.searchChatHistory(keyword);
    }

    @Data
    public static class ChatRequest {
        private String prompt;
    }

    @Data
    public static class ChatResponse {
        private final String response;
    }

    @Data
    public static class EmbeddingRequest {
        private String text;
    }

    @Data
    public static class EmbeddingResponse {
        private final List<Double> embedding;
    }
} 