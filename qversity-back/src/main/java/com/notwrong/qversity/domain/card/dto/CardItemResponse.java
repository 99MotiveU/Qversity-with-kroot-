package com.notwrong.qversity.domain.card.dto;

import com.notwrong.qversity.domain.card.entity.CardItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CardItemResponse {
    private Long cardId;
    private Long deckId;
    private String frontContent;
    private String backContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CardItemResponse from(CardItem card) {
        return CardItemResponse.builder()
                .cardId(card.getCardId())
                .deckId(card.getDeck().getDeckId())
                .frontContent(card.getFrontContent())
                .backContent(card.getBackContent())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }
}
