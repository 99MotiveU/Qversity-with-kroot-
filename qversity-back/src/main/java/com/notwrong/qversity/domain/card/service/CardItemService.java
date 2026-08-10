package com.notwrong.qversity.domain.card.service;

import com.notwrong.qversity.domain.card.dto.CardItemRequest;
import com.notwrong.qversity.domain.card.dto.CardItemResponse;

import java.util.List;

public interface CardItemService {
    List<CardItemResponse> getCardsByDeck(Long deckId, Long userId);
    CardItemResponse addCard(Long deckId, Long userId, CardItemRequest request);
    CardItemResponse updateCard(Long cardId, Long userId, CardItemRequest request);
    void deleteCard(Long cardId, Long userId);
}
