package com.example.nlp.repository;

import com.example.nlp.entity.SavedTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavedTranslationRepository extends JpaRepository<SavedTranslation, UUID> {
    boolean existsByUserIdAndTranslationId(UUID userId, UUID translationId);
    void deleteByUserIdAndTranslationId(UUID userId, UUID translationId);
}
