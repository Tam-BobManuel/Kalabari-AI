package com.example.nlp.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BatchTranslateRequest(
        @NotEmpty List<TranslateItem> items,
        String model
) {
    public record TranslateItem(
            String text,
            String sourceLanguage,
            String targetLanguage
    ) {}
}
