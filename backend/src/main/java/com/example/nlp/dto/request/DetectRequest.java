package com.example.nlp.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DetectRequest(
        @NotBlank String text
) {}
