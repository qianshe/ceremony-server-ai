package com.qianshe.ceremonyserverai.service;

import com.qianshe.ceremonyserverai.model.Tool;
import com.qianshe.ceremonyserverai.model.ToolCall;
import com.qianshe.ceremonyserverai.model.ToolCallResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class McpService {
    
    private final Map<String, Function<Map<String, Object>, Object>> tools = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate;
    
    @Autowired
    public McpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        
        // 注册内置工具
        registerTool("search_database", this::searchDatabase);
        registerTool("fetch_weather", this::fetchWeather);
        registerTool("search_web", this::searchWeb);
        registerTool("get_current_time", this::getCurrentTime);
    }
    
    public void registerTool(String name, Function<Map<String, Object>, Object> handler) {
        tools.put(name, handler);
    }
    
    public List<Tool> getAvailableTools() {
        // 创建工具列表及其描述
        return List.of(
            new Tool("search_database", "在数据库中搜索信息", 
                     Map.of("query", "搜索查询字符串", "limit", "结果数量限制")),
            new Tool("fetch_weather", "获取指定城市的天气信息", 
                     Map.of("city", "城市名称")),
            new Tool("search_web", "在网络上搜索信息", 
                     Map.of("query", "搜索查询字符串")),
            new Tool("get_current_time", "获取当前时间", 
                     Map.of("timezone", "时区，例如：Asia/Shanghai"))
        );
    }
    
    public ToolCallResult executeToolCall(ToolCall toolCall) {
        String toolName = toolCall.getName();
        Function<Map<String, Object>, Object> handler = tools.get(toolName);
        
        if (handler == null) {
            throw new IllegalArgumentException("未知的工具: " + toolName);
        }
        
        Object result = handler.apply(toolCall.getParameters());
        return new ToolCallResult(toolCall.getId(), result);
    }
    
    // 工具实现方法
    private Object searchDatabase(Map<String, Object> params) {
        String query = (String) params.get("query");
        Integer limit = params.containsKey("limit") ? (Integer) params.get("limit") : 10;
        
        // 实际实现中应该连接到数据库并执行查询
        Map<String, Object> result = new HashMap<>();
        result.put("message", "执行数据库查询: " + query + ", 限制: " + limit);
        result.put("results", List.of("结果1", "结果2", "结果3").subList(0, Math.min(3, limit)));
        
        return result;
    }
    
    private Object fetchWeather(Map<String, Object> params) {
        String city = (String) params.get("city");
        
        // 实际实现应该调用气象API
        Map<String, Object> result = new HashMap<>();
        result.put("city", city);
        result.put("temperature", "25°C");
        result.put("condition", "晴天");
        result.put("humidity", "60%");
        
        return result;
    }
    
    private Object searchWeb(Map<String, Object> params) {
        String query = (String) params.get("query");
        
        // 实际实现应该调用搜索API
        Map<String, Object> result = new HashMap<>();
        result.put("query", query);
        result.put("results", List.of(
            Map.of("title", "结果1", "url", "https://example.com/1", "snippet", "这是第一个结果..."),
            Map.of("title", "结果2", "url", "https://example.com/2", "snippet", "这是第二个结果...")
        ));
        
        return result;
    }
    
    private Object getCurrentTime(Map<String, Object> params) {
        String timezone = (String) params.getOrDefault("timezone", "Asia/Shanghai");
        
        // 实际实现中应该根据时区返回时间
        Map<String, Object> result = new HashMap<>();
        result.put("timezone", timezone);
        result.put("time", java.time.ZonedDateTime.now(java.time.ZoneId.of(timezone)).toString());
        
        return result;
    }
} 