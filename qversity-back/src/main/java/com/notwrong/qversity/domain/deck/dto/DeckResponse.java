package com.notwrong.qversity.domain.deck.dto;

import com.notwrong.qversity.domain.deck.entity.Deck;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DeckResponse {
    private Long deckId;
    private String name;
    private String description;
    private boolean isPublic;
    private String ownerNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int totalCards;

    public static DeckResponse from(Deck deck) {
        return DeckResponse.builder()
                .deckId(deck.getDeckId())
                .name(deck.getName())
                .description(deck.getDescription())
                .isPublic(deck.isPublic())
                .ownerNickname(deck.getOwner().getNickname())
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .totalCards(0)
                .build();
    }

    public DeckResponse withTotalCards(int count) {
        return DeckResponse.builder()
                .deckId(this.deckId)
                .name(this.name)
                .description(this.description)
                .isPublic(this.isPublic)
                .ownerNickname(this.ownerNickname)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .totalCards(count)
                .build();
    }
}
