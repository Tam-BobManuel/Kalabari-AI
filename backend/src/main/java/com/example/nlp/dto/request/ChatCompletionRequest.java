package com.example.nlp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ChatCompletionRequest(
        @NotBlank String model,
        @NotEmpty List<Message> messages,
        Double temperature,
        @Positive Integer maxTokens,
        Boolean stream
) {
    public record Message(
            @NotBlank String role,
            @NotBlank String content
    ) {}
}
