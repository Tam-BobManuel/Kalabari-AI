package com.example.nlp.controller;

import com.example.nlp.dto.request.GenerateRequest;
import com.example.nlp.dto.response.GenerateResponse;
import com.example.nlp.service.GenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Generation", description = "Generate text using the AI model")
public class GenerateController {

    private final GenerationService generationService;

    public GenerateController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate text from a prompt")
    public ResponseEntity<GenerateResponse> generate(@Valid @RequestBody GenerateRequest request) {
        var response = generationService.generate(request);
        return ResponseEntity.ok(response);
    }
}
