package com.example.nlp.client;

import com.example.nlp.dto.request.ChatCompletionRequest;
import com.example.nlp.dto.response.ChatCompletionResponse;

public interface Gemma4Client {
    ChatCompletionResponse chatCompletion(ChatCompletionRequest request);
}
