package com.example.nlp.dto.response;

public record DetectResponse(
        String language,
        String languageName,
        double confidence
) {}
