package com.example.nlp.controller;

import com.example.nlp.entity.TranslationHistory;
import com.example.nlp.service.TranslationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/translations")
@Tag(name = "History", description = "User translation history management")
public class TranslationHistoryController {

    private final TranslationHistoryService historyService;

    public TranslationHistoryController(TranslationHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    @Operation(summary = "Get paginated translation history")
    public ResponseEntity<Page<TranslationHistory>> getHistory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(historyService.getHistory(userId, page, size));
    }

    @PostMapping
    @Operation(summary = "Save a translation to history")
    public ResponseEntity<TranslationHistory> save(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody TranslationHistory entry
    ) {
        entry.setUserId(UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(historyService.save(entry));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a translation from history")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        historyService.delete(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Get translation statistics")
    public ResponseEntity<Map<String, Object>> stats(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(Map.of(
                "total", historyService.countTotal(userId),
                "today", historyService.countToday(userId)
        ));
    }
}
