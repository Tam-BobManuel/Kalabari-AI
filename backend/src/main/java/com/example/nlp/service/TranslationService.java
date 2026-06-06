package com.example.nlp.service;

import com.example.nlp.client.Gemma4Client;
import com.example.nlp.dto.request.ChatCompletionRequest;
import com.example.nlp.dto.request.TranslateRequest;
import com.example.nlp.dto.response.TranslateResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranslationService {

    private static final String DEFAULT_MODEL = "gemma-4-e4b-nigerian";
    private static final String SYSTEM_PROMPT = """
            You are a Nigerian language translation expert. Translate the given text accurately.
            Preserve the meaning, tone, and cultural context. Respond with only the translated text.
            """;

    private final Gemma4Client gemma4Client;

    public TranslationService(Gemma4Client gemma4Client) {
        this.gemma4Client = gemma4Client;
    }

    @Cacheable(value = "translations", key = "#request.text + ':' + #request.sourceLanguage + ':' + #request.targetLanguage")
    public TranslateResponse translate(TranslateRequest request) {
        var userPrompt = "Translate the following text from " + request.sourceLanguage()
                + " to " + request.targetLanguage() + ":\n\n" + request.text();

        var chatRequest = new ChatCompletionRequest(
                request.model() != null ? request.model() : DEFAULT_MODEL,
                List.of(
                        new ChatCompletionRequest.Message("system", SYSTEM_PROMPT),
                        new ChatCompletionRequest.Message("user", userPrompt)
                ),
                0.3,
                1024,
                false
        );

        var response = gemma4Client.chatCompletion(chatRequest);
        var translatedText = response.choices().getFirst().message().content();

        return new TranslateResponse(
                translatedText,
                request.sourceLanguage(),
                request.targetLanguage(),
                chatRequest.model()
        );
    }
}
