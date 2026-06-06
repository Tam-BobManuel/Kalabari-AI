package com.example.nlp.controller;

import com.example.nlp.entity.Dataset;
import com.example.nlp.service.DatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/datasets")
@Tag(name = "Datasets", description = "Community dataset contributions")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping
    @Operation(summary = "List user's uploaded datasets")
    public ResponseEntity<List<Dataset>> list(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(datasetService.getUserDatasets(userId));
    }

    @PostMapping
    @Operation(summary = "Register a dataset upload (file stored externally)")
    public ResponseEntity<Dataset> upload(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Dataset dataset
    ) {
        dataset.setUserId(UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(datasetService.save(dataset));
    }
}
