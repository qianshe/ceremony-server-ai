package com.qianshe.ceremonyserverai.service;

import com.qianshe.ceremonyserverai.entity.ChatHistory;
import com.qianshe.ceremonyserverai.model.*;
import com.qianshe.ceremonyserverai.repository.ChatHistoryRepository;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final EmbeddingClient embeddingClient;
    private final ChatHistoryRepository chatHistoryRepository;
    private final McpService mcpService;

    @Autowired
    public AiService(ChatClient chatClient, EmbeddingClient embeddingClient,
                     ChatHistoryRepository chatHistoryRepository, McpService mcpService) {
        this.chatClient = chatClient;
        this.embeddingClient = embeddingClient;
        this.chatHistoryRepository = chatHistoryRepository;
        this.mcpService = mcpService;
    }

    public String chat(String prompt) {
        // 准备系统提示，告知AI可用的工具
        List<Tool> tools = mcpService.getAvailableTools();
        String systemPrompt = "你是一个智能助手，可以回答用户问题。如果需要调用外部工具获取信息，请使用以下格式：\n" +
            "toolCall: 工具名称, 参数: {key1:value1, key2:value2}\n\n" +
            "可用的工具有：\n" + 
            tools.stream()
                .map(tool -> tool.getName() + ": " + tool.getDescription())
                .collect(Collectors.joining("\n"));
        
        // 创建包含系统提示的消息
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        messages.add(new UserMessage(prompt));
        
        // 调用模型
        Prompt aiPrompt = new Prompt(messages);
        ChatResponse chatResponse = chatClient.call(aiPrompt);
        String initialResponse = chatResponse.getResult().getOutput().getContent();
        
        // 解析工具调用
        List<ToolCall> toolCalls = parseToolCalls(initialResponse);
        
        // 如果存在工具调用请求
        if (!toolCalls.isEmpty()) {
            StringBuilder finalResponse = new StringBuilder();
            for (ToolCall toolCall : toolCalls) {
                try {
                    // 执行工具调用
                    ToolCallResult result = mcpService.executeToolCall(toolCall);
                    
                    // 将工具调用结果添加到消息列表
                    ChatMessage toolMessage = new ChatMessage();
                    toolMessage.setRole("tool");
                    toolMessage.setToolCallId(toolCall.getId());
                    toolMessage.setContent(result.getResult().toString());
                    
                    // 创建新的消息列表，包括工具响应
                    List<Message> updatedMessages = new ArrayList<>(messages);
                    updatedMessages.add(new UserMessage("工具调用结果：\n工具：" + toolCall.getName() + 
                                                      "\n结果：" + result.getResult().toString()));
                    
                    // 再次调用模型，生成最终响应
                    ChatResponse finalChatResponse = chatClient.call(new Prompt(updatedMessages));
                    String toolBasedResponse = finalChatResponse.getResult().getOutput().getContent();
                    
                    // 修改返回格式，使结果看起来更加自然
                    finalResponse.append(toolBasedResponse);
                } catch (Exception e) {
                    finalResponse.append("调用工具 ").append(toolCall.getName()).append(" 时发生错误: ").append(e.getMessage());
                }
            }
            
            // 保存对话历史
            ChatHistory chatHistory = new ChatHistory(prompt, finalResponse.toString());
            chatHistoryRepository.save(chatHistory);
            
            return finalResponse.toString();
        }
        
        // 没有工具调用，返回原始响应
        ChatHistory chatHistory = new ChatHistory(prompt, initialResponse);
        chatHistoryRepository.save(chatHistory);
        
        return initialResponse;
    }
    
    public ChatCompletionResponse chatWithMcp(ChatCompletionRequest request) {
        List<ChatMessage> messages = request.getMessages();
        List<Tool> tools = mcpService.getAvailableTools();
        
        // 处理带有工具调用的对话
        String responseContent = null;
        List<ToolCall> toolCalls = null;
        
        // 如果模型要求使用工具
        if (messages.size() > 0 && request.isToolChoice()) {
            // 将消息转换为Spring AI格式
            List<Message> aiMessages = convertToAiMessages(messages);
            
            // 添加系统提示，说明可用工具
            String systemPrompt = "你有以下工具可用：\n" + 
                tools.stream()
                    .map(tool -> tool.getName() + ": " + tool.getDescription())
                    .collect(Collectors.joining("\n"));
            
            aiMessages.add(0, new SystemMessage(systemPrompt));
            
            // 调用模型
            ChatResponse chatResponse = chatClient.call(new Prompt(aiMessages));
            responseContent = chatResponse.getResult().getOutput().getContent();
            
            // 解析响应中的工具调用
            toolCalls = parseToolCalls(responseContent);
            
            // 如果有工具调用请求，执行这些工具
            if (toolCalls != null && !toolCalls.isEmpty()) {
                List<ChatMessage> updatedMessages = new ArrayList<>(messages);
                
                // 创建助手消息，包含工具调用
                ChatMessage assistantMessage = new ChatMessage();
                assistantMessage.setRole("assistant");
                assistantMessage.setContent(null);  // 工具调用时内容设为null
                assistantMessage.setToolCalls(toolCalls);
                updatedMessages.add(assistantMessage);
                
                // 执行每个工具并添加结果
                for (ToolCall toolCall : toolCalls) {
                    ToolCallResult result = mcpService.executeToolCall(toolCall);
                    
                    // 添加工具响应消息
                    ChatMessage toolMessage = new ChatMessage();
                    toolMessage.setRole("tool");
                    toolMessage.setToolCallId(toolCall.getId());
                    toolMessage.setContent(result.getResult().toString());
                    updatedMessages.add(toolMessage);
                }
                
                // 使用更新后的消息再次调用模型
                List<Message> updatedAiMessages = convertToAiMessages(updatedMessages);
                ChatResponse finalResponse = chatClient.call(new Prompt(updatedAiMessages));
                responseContent = finalResponse.getResult().getOutput().getContent();
            }
        } else {
            // 常规对话，不使用工具
            List<Message> aiMessages = convertToAiMessages(messages);
            ChatResponse chatResponse = chatClient.call(new Prompt(aiMessages));
            responseContent = chatResponse.getResult().getOutput().getContent();
        }
        
        // 创建最终响应
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(responseContent);
        if (toolCalls != null && !toolCalls.isEmpty()) {
            assistantMessage.setToolCalls(toolCalls);
        }
        
        ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice(
            0, assistantMessage, "stop");
        
        ChatCompletionResponse response = new ChatCompletionResponse(
            UUID.randomUUID().toString(),
            request.getModel() != null ? request.getModel() : "deepseek-r1:8b",
            Collections.singletonList(choice)
        );
        
        // 保存对话历史
        String userPrompt = extractUserPrompt(messages);
        if (userPrompt != null) {
            ChatHistory chatHistory = new ChatHistory(userPrompt, responseContent);
            chatHistoryRepository.save(chatHistory);
        }
        
        return response;
    }

    public List<Double> getEmbedding(String text) {
        return embeddingClient.embed(text);
    }
    
    public List<ChatHistory> getRecentChatHistory() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        return chatHistoryRepository.findByTimestampBetween(oneDayAgo, LocalDateTime.now());
    }
    
    public List<ChatHistory> searchChatHistory(String keyword) {
        return chatHistoryRepository.findByUserPromptContaining(keyword);
    }
    
    // 辅助方法
    
    private List<Message> convertToAiMessages(List<ChatMessage> messages) {
        return messages.stream().map(msg -> {
            if ("system".equals(msg.getRole())) {
                return new SystemMessage(msg.getContent());
            } else if ("user".equals(msg.getRole())) {
                return new UserMessage(msg.getContent());
            } else {
                // 处理assistant和tool消息，根据Spring AI支持情况调整
                return new UserMessage("Role: " + msg.getRole() + "\n" + msg.getContent());
            }
        }).collect(Collectors.toList());
    }
    
    private List<ToolCall> parseToolCalls(String content) {
        // 解析工具调用格式: "toolCall: 工具名称, 参数: {key:value}"
        List<ToolCall> toolCalls = new ArrayList<>();
        
        if (content.contains("toolCall:")) {
            // 分割响应文本找到所有工具调用
            String[] parts = content.split("toolCall:");
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i].trim();
                int commaIndex = part.indexOf(',');
                if (commaIndex > 0) {
                    String toolName = part.substring(0, commaIndex).trim();
                    
                    // 提取参数部分
                    int paramStart = part.indexOf('{');
                    int paramEnd = part.indexOf('}');
                    
                    if (paramStart >= 0 && paramEnd > paramStart) {
                        String paramStr = part.substring(paramStart, paramEnd + 1);
                        
                        // 简单参数解析，实际应用中应使用JSON解析
                        Map<String, Object> params = new HashMap<>();
                        if (paramStr.contains(":")) {
                            String[] paramParts = paramStr.substring(1, paramStr.length() - 1).split(",");
                            for (String param : paramParts) {
                                String[] keyValue = param.split(":");
                                if (keyValue.length == 2) {
                                    params.put(keyValue[0].trim(), keyValue[1].trim());
                                }
                            }
                        }
                        
                        ToolCall toolCall = new ToolCall();
                        toolCall.setId(UUID.randomUUID().toString());
                        toolCall.setName(toolName);
                        toolCall.setParameters(params);
                        toolCalls.add(toolCall);
                    }
                }
            }
        }
        
        return toolCalls;
    }
    
    private String extractUserPrompt(List<ChatMessage> messages) {
        // 从消息列表中提取最后一个用户消息
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatMessage msg = messages.get(i);
            if ("user".equals(msg.getRole())) {
                return msg.getContent();
            }
        }
        return null;
    }
} 