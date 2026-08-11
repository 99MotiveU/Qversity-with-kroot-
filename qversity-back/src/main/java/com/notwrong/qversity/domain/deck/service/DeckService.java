package com.notwrong.qversity.domain.deck.service;

import com.notwrong.qversity.domain.deck.dto.DeckRequest;
import com.notwrong.qversity.domain.deck.dto.DeckResponse;
import com.notwrong.qversity.domain.deck.entity.Deck;

import java.util.List;

public interface DeckService {
    List<DeckResponse> getMyDecks(Long userId);
    DeckResponse getDeck(Long deckId, Long userId);
    DeckResponse createDeck(Long userId, DeckRequest request);
    DeckResponse updateDeck(Long deckId, Long userId, DeckRequest request);
    void deleteDeck(Long deckId, Long userId);
    Deck findById(Long deckId);
}
