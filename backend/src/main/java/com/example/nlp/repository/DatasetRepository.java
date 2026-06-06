package com.example.nlp.repository;

import com.example.nlp.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DatasetRepository extends JpaRepository<Dataset, UUID> {
    List<Dataset> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
