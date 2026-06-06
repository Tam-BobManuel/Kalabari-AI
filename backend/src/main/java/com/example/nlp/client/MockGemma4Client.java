package com.example.nlp.client;

import com.example.nlp.dto.request.ChatCompletionRequest;
import com.example.nlp.dto.response.ChatCompletionResponse;
import com.example.nlp.dto.response.Choice;
import com.example.nlp.dto.response.Usage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("mockGemma4Client")
@ConditionalOnProperty(name = "model.server.mock", havingValue = "true", matchIfMissing = false)
public class MockGemma4Client implements Gemma4Client {

    @Override
    public ChatCompletionResponse chatCompletion(ChatCompletionRequest request) {
        var userMessage = request.messages().stream()
                .filter(m -> "user".equals(m.role()))
                .findFirst()
                .map(m -> m.content())
                .orElse("");

        var reply = "Mock reply for: \"" + userMessage + "\" [Nigerian language response]";

        var choice = new Choice(
                0,
                new ChatCompletionResponse.Message("assistant", reply),
                "stop"
        );

        return new ChatCompletionResponse(
                "chatcmpl-mock",
                List.of(choice),
                new Usage(10, 20, 30)
        );
    }
}
