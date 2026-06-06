package com.example.nlp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TranslateRequest(
        @NotBlank String text,
        @NotBlank String sourceLanguage,
        @NotBlank String targetLanguage,
        String model
) {}
