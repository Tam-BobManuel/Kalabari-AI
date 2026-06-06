package com.example.nlp.service;

import com.example.nlp.client.Gemma4Client;
import com.example.nlp.dto.request.ChatCompletionRequest;
import com.example.nlp.dto.request.DetectRequest;
import com.example.nlp.dto.response.DetectResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DetectionService {

    private static final String DETECT_PROMPT = """
            Identify the Nigerian language of the following text.
            Respond with ONLY the language code (e.g., 'ibo' for Igbo, 'yor' for Yoruba, 'hau' for Hausa,
            'pcm' for Nigerian Pidgin, 'kal' for Kalabari, 'ful' for Fulfulde, 'ibo' for Igbo,
            'tiv' for Tiv, 'efi' for Efik, 'ibb' for Ibibio, 'eng' for English).
            If uncertain, respond with the most likely language code.
            """;

    private static final Map<String, String> LANGUAGE_NAMES = Map.ofEntries(
            Map.entry("ibo", "Igbo"),
            Map.entry("yor", "Yoruba"),
            Map.entry("hau", "Hausa"),
            Map.entry("pcm", "Nigerian Pidgin"),
            Map.entry("kal", "Kalabari"),
            Map.entry("ful", "Fulfulde"),
            Map.entry("tiv", "Tiv"),
            Map.entry("efi", "Efik"),
            Map.entry("ibb", "Ibibio"),
            Map.entry("eng", "English")
    );

    private final Gemma4Client gemma4Client;

    public DetectionService(Gemma4Client gemma4Client) {
        this.gemma4Client = gemma4Client;
    }

    @Cacheable(value = "detections", key = "#request.text")
    public DetectResponse detect(DetectRequest request) {
        var chatRequest = new ChatCompletionRequest(
                "gemma-4-e4b-nigerian",
                List.of(
                        new ChatCompletionRequest.Message("system", DETECT_PROMPT),
                        new ChatCompletionRequest.Message("user", request.text())
                ),
                0.1,
                10,
                false
        );

        var response = gemma4Client.chatCompletion(chatRequest);
        var languageCode = response.choices().getFirst().message().content().trim().toLowerCase();
        var languageName = LANGUAGE_NAMES.getOrDefault(languageCode, "Unknown");

        return new DetectResponse(languageCode, languageName, 0.85);
    }
}
