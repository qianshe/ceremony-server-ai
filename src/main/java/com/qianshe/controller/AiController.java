package com.qianshe.controller;

import com.qianshe.tools.DateTimeTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RequiredArgsConstructor
@RestController
public class AiController {

    final ChatClient chatClient;

    final ToolCallback toolCallback;

    @GetMapping(value = "/ai/chat")
    public String chat(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .call().content();
    }
    // 流式响应
    // 需要在请求头中添加 Accept: text/event-stream
    // 需要在请求头中添加 Content-Type: application/json
    // 需要在请求体中添加 {"message": "你好"}
    // 需要在请求体中添加 {"message": "你好", "stream": true}
    @GetMapping(value = "/ai/chat/stream", produces = "text/event-stream;charset=utf-8")
    public Flux<String> chatStream(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .stream().content();
    }

    // deepseek-r1:8b does not support tools
    // Methods as Tools
    @GetMapping(value = "/ai/chat/tools")
    public String chatTools(@RequestParam("message") String message) {
        return chatClient.prompt(message)
                .tools(new DateTimeTools())
                .call().content();
    }

    // Functions as Tools
    @GetMapping(value = "/ai/chat/func")
    public String chatFunc(@RequestParam("message") String message) {
        return chatClient.prompt(message)
                .tools(toolCallback)
                .call().content();
    }
    // Functions as Tools
    @GetMapping(value = "/ai/chat/func/bean")
    public String chatFuncBean(@RequestParam("message") String message) {
        return chatClient.prompt(message)
                .tools("currentWeather")
                .call().content();
    }

} 