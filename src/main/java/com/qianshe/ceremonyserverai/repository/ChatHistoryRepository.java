package com.qianshe.ceremonyserverai.repository;

import com.qianshe.ceremonyserverai.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    
    List<ChatHistory> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    List<ChatHistory> findByUserPromptContaining(String keyword);
} 