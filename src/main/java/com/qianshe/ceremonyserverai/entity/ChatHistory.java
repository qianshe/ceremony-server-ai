package com.qianshe.ceremonyserverai.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userPrompt;
    
    private String aiResponse;
    
    private LocalDateTime timestamp;
    
    public ChatHistory(String userPrompt, String aiResponse) {
        this.userPrompt = userPrompt;
        this.aiResponse = aiResponse;
        this.timestamp = LocalDateTime.now();
    }
} 