package com.example.nlp.service;

import com.example.nlp.client.Gemma4Client;
import com.example.nlp.dto.request.ChatCompletionRequest;
import com.example.nlp.dto.request.GenerateRequest;
import com.example.nlp.dto.response.GenerateResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenerationService {

    private static final String DEFAULT_MODEL = "gemma-4-e4b-nigerian";

    private final Gemma4Client gemma4Client;

    public GenerationService(Gemma4Client gemma4Client) {
        this.gemma4Client = gemma4Client;
    }

    public GenerateResponse generate(GenerateRequest request) {
        var chatRequest = new ChatCompletionRequest(
                request.model() != null ? request.model() : DEFAULT_MODEL,
                List.of(new ChatCompletionRequest.Message("user", request.prompt())),
                request.temperature(),
                request.maxTokens(),
                false
        );

        var response = gemma4Client.chatCompletion(chatRequest);
        var generatedText = response.choices().getFirst().message().content();

        return new GenerateResponse(generatedText, chatRequest.model());
    }
}
