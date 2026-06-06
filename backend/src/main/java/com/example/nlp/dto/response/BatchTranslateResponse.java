package com.example.nlp.dto.response;

import java.util.List;

public record BatchTranslateResponse(
        List<Item> items,
        int totalProcessed,
        int failedCount
) {
    public record Item(
            int index,
            String translatedText,
            String error
    ) {}
}
