package com.example.nlp.repository;

import com.example.nlp.entity.TranslationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TranslationHistoryRepository extends JpaRepository<TranslationHistory, UUID> {
    Page<TranslationHistory> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByUserId(UUID userId);
    long countByUserIdAndCreatedAtAfter(UUID userId, java.time.Instant after);
    void deleteByIdAndUserId(UUID id, UUID userId);
}
