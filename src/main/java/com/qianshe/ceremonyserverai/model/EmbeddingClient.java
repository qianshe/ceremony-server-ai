package com.qianshe.ceremonyserverai.model;

import java.util.List;

/**
 * 文本嵌入处理接口
 */
public interface EmbeddingClient {
    
    /**
     * 生成文本嵌入向量
     * @param text 输入文本
     * @return 嵌入向量
     */
    List<Double> embed(String text);
} 