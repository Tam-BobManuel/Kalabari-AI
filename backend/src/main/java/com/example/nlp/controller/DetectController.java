package com.example.nlp.controller;

import com.example.nlp.dto.request.DetectRequest;
import com.example.nlp.dto.response.DetectResponse;
import com.example.nlp.service.DetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Detection", description = "Detect Nigerian languages in text")
public class DetectController {

    private final DetectionService detectionService;

    public DetectController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping("/detect")
    @Operation(summary = "Detect the language of a given text")
    public ResponseEntity<DetectResponse> detect(@Valid @RequestBody DetectRequest request) {
        var response = detectionService.detect(request);
        return ResponseEntity.ok(response);
    }
}
