package com.example.nlp.dto.response;

public record GenerateResponse(
        String generatedText,
        String modelUsed
) {}
