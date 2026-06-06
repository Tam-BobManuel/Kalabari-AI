package com.example.nlp.service;

import com.example.nlp.entity.Dataset;
import com.example.nlp.repository.DatasetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DatasetService {

    private final DatasetRepository repository;

    public DatasetService(DatasetRepository repository) {
        this.repository = repository;
    }

    public List<Dataset> getUserDatasets(UUID userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Dataset save(Dataset dataset) {
        return repository.save(dataset);
    }
}
