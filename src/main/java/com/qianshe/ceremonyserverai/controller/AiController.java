package com.qianshe.ceremonyserverai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AiController {

    final ChatClient chatClient;

    @GetMapping("/ai/chat")
    public String chat(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .call().content();
    }
} 