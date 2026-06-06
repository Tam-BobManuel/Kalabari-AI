package com.example.nlp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatCompletionResponse(
        String id,
        List<Choice> choices,
        Usage usage
) {
    public record Message(
            String role,
            String content
    ) {}
}
