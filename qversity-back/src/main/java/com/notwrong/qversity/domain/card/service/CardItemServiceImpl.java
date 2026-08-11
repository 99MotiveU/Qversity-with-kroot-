package com.notwrong.qversity.domain.card.service;

import com.notwrong.qversity.domain.card.dto.CardItemRequest;
import com.notwrong.qversity.domain.card.dto.CardItemResponse;
import com.notwrong.qversity.domain.card.entity.CardItem;
import com.notwrong.qversity.domain.card.repository.CardItemRepository;
import com.notwrong.qversity.domain.deck.entity.Deck;
import com.notwrong.qversity.domain.deck.service.DeckService;
import com.notwrong.qversity.global.exception.BaseException;
import com.notwrong.qversity.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardItemServiceImpl implements CardItemService {

    private final CardItemRepository cardItemRepository;
    private final DeckService deckService;

    @Override
    public List<CardItemResponse> getCardsByDeck(Long deckId, Long userId) {
        deckService.getDeck(deckId, userId); // validates access
        return cardItemRepository.findByDeck_DeckId(deckId).stream()
                .map(CardItemResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardItemResponse addCard(Long deckId, Long userId, CardItemRequest request) {
        Deck deck = deckService.findById(deckId);
        if (!deck.getOwner().getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.DECK_ACCESS_DENIED);
        }
        CardItem card = CardItem.builder()
                .deck(deck)
                .frontContent(request.getFrontContent())
                .backContent(request.getBackContent())
                .build();
        return CardItemResponse.from(cardItemRepository.save(card));
    }

    @Override
    @Transactional
    public CardItemResponse updateCard(Long cardId, Long userId, CardItemRequest request) {
        CardItem card = cardItemRepository.findById(cardId)
                .orElseThrow(() -> new BaseException(ErrorCode.CARD_NOT_FOUND));
        if (!card.getDeck().getOwner().getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.DECK_ACCESS_DENIED);
        }
        card.setFrontContent(request.getFrontContent());
        card.setBackContent(request.getBackContent());
        return CardItemResponse.from(cardItemRepository.save(card));
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId, Long userId) {
        CardItem card = cardItemRepository.findById(cardId)
                .orElseThrow(() -> new BaseException(ErrorCode.CARD_NOT_FOUND));
        if (!card.getDeck().getOwner().getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.DECK_ACCESS_DENIED);
        }
        cardItemRepository.delete(card);
    }
}
