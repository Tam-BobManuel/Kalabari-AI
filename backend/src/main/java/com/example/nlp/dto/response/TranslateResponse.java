package com.example.nlp.dto.response;

public record TranslateResponse(
        String translatedText,
        String sourceLanguage,
        String targetLanguage,
        String modelUsed
) {}
