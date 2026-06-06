package com.example.nlp.controller;

import com.example.nlp.dto.request.BatchTranslateRequest;
import com.example.nlp.dto.request.TranslateRequest;
import com.example.nlp.dto.response.BatchTranslateResponse;
import com.example.nlp.dto.response.TranslateResponse;
import com.example.nlp.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Translation", description = "Translate text between Nigerian languages")
public class TranslateController {

    private final TranslationService translationService;

    public TranslateController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/translate")
    @Operation(summary = "Translate text between languages")
    public ResponseEntity<TranslateResponse> translate(@Valid @RequestBody TranslateRequest request) {
        var response = translationService.translate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/translate/batch")
    @Operation(summary = "Batch translate multiple texts")
    public ResponseEntity<BatchTranslateResponse> batchTranslate(@Valid @RequestBody BatchTranslateRequest request) {
        var items = new ArrayList<BatchTranslateResponse.Item>();
        int failed = 0;

        for (int i = 0; i < request.items().size(); i++) {
            var item = request.items().get(i);
            try {
                var result = translationService.translate(
                        new TranslateRequest(item.text(), item.sourceLanguage(), item.targetLanguage(), request.model())
                );
                items.add(new BatchTranslateResponse.Item(i, result.translatedText(), null));
            } catch (Exception e) {
                items.add(new BatchTranslateResponse.Item(i, null, e.getMessage()));
                failed++;
            }
        }

        return ResponseEntity.ok(new BatchTranslateResponse(items, items.size(), failed));
    }
}
