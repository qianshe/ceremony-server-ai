package com.qianshe.controller;

import com.qianshe.tools.DateTimeTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RequiredArgsConstructor
@RestController
public class AiController {

    final ChatClient chatClient;

    @GetMapping(value = "/ai/chat", produces = "text/event-stream;charset=utf-8")
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
    public Flux<ChatResponse> chatStream(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .stream().chatResponse();
    }

    // deepseek-r1:8b does not support tools
    // function call 也属于 tools 所以不支持
    @GetMapping(value = "/ai/chat/tools", produces = "text/event-stream;charset=utf-8")
    public String chatTools(@RequestParam("message") String message) {
        return chatClient.prompt(message)
                .tools(new DateTimeTools())
                .call().content();
    }

    @Deprecated
    @GetMapping(value = "/ai/chat/func", produces = "text/event-stream;charset=utf-8")
    public String chatFunc(@RequestParam("message") String message) {
        return chatClient.prompt(message)
                .functions("getCurrentDateTime")
                .call().content();
    }

} 