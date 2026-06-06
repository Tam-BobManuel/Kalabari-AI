package com.example.nlp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GenerateRequest(
        @NotBlank String prompt,
        Double temperature,
        Integer maxTokens,
        String model
) {}
