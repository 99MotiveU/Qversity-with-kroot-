package com.notwrong.qversity.domain.card.controller;

import com.notwrong.qversity.domain.card.dto.CardItemRequest;
import com.notwrong.qversity.domain.card.dto.CardItemResponse;
import com.notwrong.qversity.domain.card.service.CardItemService;
import com.notwrong.qversity.global.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardItemService cardItemService;

    @GetMapping("/api/decks/{deckId}/cards")
    public ResponseEntity<Map<String, Object>> getCards(
            @PathVariable Long deckId,
            @AuthenticationPrincipal CustomUserDetails user) {
        List<CardItemResponse> cards = cardItemService.getCardsByDeck(deckId, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true, "data", cards));
    }

    @PostMapping("/api/decks/{deckId}/cards")
    public ResponseEntity<Map<String, Object>> addCard(
            @PathVariable Long deckId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CardItemRequest request) {
        CardItemResponse card = cardItemService.addCard(deckId, user.getUserId(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", card));
    }

    @PutMapping("/api/cards/{cardId}")
    public ResponseEntity<Map<String, Object>> updateCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CardItemRequest request) {
        CardItemResponse card = cardItemService.updateCard(cardId, user.getUserId(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", card));
    }

    @DeleteMapping("/api/cards/{cardId}")
    public ResponseEntity<Map<String, Object>> deleteCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal CustomUserDetails user) {
        cardItemService.deleteCard(cardId, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
