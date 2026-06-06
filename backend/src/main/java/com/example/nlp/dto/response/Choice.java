package com.example.nlp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Choice(
        int index,
        ChatCompletionResponse.Message message,
        @JsonProperty("finish_reason") String finishReason
) {}
