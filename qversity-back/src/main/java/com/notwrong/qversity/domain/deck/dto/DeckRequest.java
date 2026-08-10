package com.notwrong.qversity.domain.deck.dto;

import lombok.Getter;

@Getter
public class DeckRequest {
    private String name;
    private String description;
    private boolean isPublic;
}
