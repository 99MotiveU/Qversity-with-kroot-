package com.notwrong.qversity.domain.study.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeckStatsResponse {
    private Long deckId;
    private int totalCards;
    private int newCards;
    private int learningCards;
    private int reviewCards;
    private int dueCards;
    private double averageRetention;
}
