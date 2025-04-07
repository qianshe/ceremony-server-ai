# 仪式服务 AI 后端

这是一个使用Spring AI和MCP能力的项目，可调用本地部署的Ollama模型（deepseek-r1:8b）进行AI对话、嵌入向量生成，并支持数据库存储和API调用功能。

## 项目需求

- JDK 17（Spring AI要求Spring Boot 3.x，而Spring Boot 3.x需要JDK 17+）
- Maven
- MySQL数据库
- Ollama（本地运行，需安装deepseek-r1:8b模型）

## 技术栈

- Spring Boot 3.2.4
- Spring AI 1.0.0（正式版）
- MySQL
- Ollama模型服务

## 安装Ollama和模型

1. 从[Ollama官网](https://ollama.ai/)下载并安装Ollama
2. 运行以下命令拉取deepseek-r1:8b模型：
   ```
   ollama pull deepseek-r1:8b
   ```

## 数据库准备

1. 安装并启动MySQL数据库
2. 创建数据库：
   ```sql
   CREATE DATABASE ceremony_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. 配置application.yml中的数据库连接信息（默认用户名密码为root/root）

## 项目结构

```
ceremony-server-ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── qianshe/
│   │   │           └── ceremonyserverai/
│   │   │               ├── config/
│   │   │               │   ├── OllamaConfig.java
│   │   │               │   └── RestTemplateConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── AiController.java
│   │   │               │   └── McpController.java
│   │   │               ├── entity/
│   │   │               │   └── ChatHistory.java
│   │   │               ├── model/
│   │   │               │   ├── ChatCompletionRequest.java
│   │   │               │   ├── ChatCompletionResponse.java
│   │   │               │   ├── ChatMessage.java
│   │   │               │   ├── EmbeddingClient.java
│   │   │               │   ├── Tool.java
│   │   │               │   ├── ToolCall.java
│   │   │               │   └── ToolCallResult.java
│   │   │               ├── repository/
│   │   │               │   └── ChatHistoryRepository.java
│   │   │               ├── service/
│   │   │               │   ├── AiService.java
│   │   │               │   └── McpService.java
│   │   │               └── CeremonyServerAiApplication.java
│   │   └── resources/
│   │       └── application.yml
├── pom.xml
└── README.md
```

## 启动项目

```bash
mvn spring-boot:run
```

## API接口

### 1. AI 对话

- **接口**: POST /api/ai/chat
- **请求体**:
  ```json
  {
    "prompt": "你好，请介绍一下自己"
  }
  ```
- **响应**:
  ```json
  {
    "response": "你好！我是一个基于deepseek-r1:8b模型的AI助手..."
  }
  ```

**特性**：AI会自动识别需要调用工具的情况，并在需要时调用MCP工具获取信息，无需用户手动指定工具。如询问"今天北京的天气怎么样？"，AI将自动调用天气工具获取信息并回复。

### 2. AI对话与MCP能力（完整的聊天完成API）

- **接口**: POST /api/ai/chat/completions
- **请求体**:
  ```json
  {
    "model": "deepseek-r1:8b",
    "messages": [
      {"role": "system", "content": "你是一个有用的AI助手"},
      {"role": "user", "content": "今天北京的天气怎么样？"}
    ],
    "tools": [],
    "toolChoice": true,
    "temperature": 0.7
  }
  ```
- **响应**:
  ```json
  {
    "id": "chat-uuid",
    "model": "deepseek-r1:8b",
    "choices": [
      {
        "index": 0,
        "message": {
          "role": "assistant",
          "content": "根据我获取的信息，今天北京的天气是晴天，温度25°C，湿度60%。"
        },
        "finishReason": "stop"
      }
    ]
  }
  ```

### 3. 生成嵌入向量

- **接口**: POST /api/ai/embed
- **请求体**:
  ```json
  {
    "text": "这是一段示例文本"
  }
  ```
- **响应**:
  ```json
  {
    "embedding": [0.123, 0.456, ...]
  }
  ```

### 4. 获取最近的对话历史

- **接口**: GET /api/ai/history/recent
- **响应**: 返回最近一天的对话历史记录列表

### 5. 搜索对话历史

- **接口**: GET /api/ai/history/search?keyword=关键词
- **响应**: 返回包含指定关键词的对话历史记录列表

## 支持的MCP工具功能

系统内置了以下工具功能，AI会根据对话需要自动调用：

1. **search_database** - 在数据库中搜索信息
2. **fetch_weather** - 获取指定城市的天气信息
3. **search_web** - 在网络上搜索信息
4. **get_current_time** - 获取当前时间，支持指定时区

## 数据库

项目使用MySQL数据库，默认配置：

- 数据库名：ceremony_ai
- 用户名：root
- 密码：root
- URL：jdbc:mysql://localhost:3306/ceremony_ai?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8 