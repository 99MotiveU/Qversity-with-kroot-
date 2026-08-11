package com.notwrong.qversity.domain.deck.controller;

import com.notwrong.qversity.domain.deck.dto.DeckRequest;
import com.notwrong.qversity.domain.deck.dto.DeckResponse;
import com.notwrong.qversity.domain.deck.service.DeckService;
import com.notwrong.qversity.global.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyDecks(@AuthenticationPrincipal CustomUserDetails user) {
        List<DeckResponse> decks = deckService.getMyDecks(user.getUserId());
        return ResponseEntity.ok(Map.of("success", true, "data", decks));
    }

    @GetMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> getDeck(
            @PathVariable Long deckId,
            @AuthenticationPrincipal CustomUserDetails user) {
        DeckResponse deck = deckService.getDeck(deckId, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true, "data", deck));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDeck(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody DeckRequest request) {
        DeckResponse deck = deckService.createDeck(user.getUserId(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", deck));
    }

    @PutMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> updateDeck(
            @PathVariable Long deckId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody DeckRequest request) {
        DeckResponse deck = deckService.updateDeck(deckId, user.getUserId(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", deck));
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Map<String, Object>> deleteDeck(
            @PathVariable Long deckId,
            @AuthenticationPrincipal CustomUserDetails user) {
        deckService.deleteDeck(deckId, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
