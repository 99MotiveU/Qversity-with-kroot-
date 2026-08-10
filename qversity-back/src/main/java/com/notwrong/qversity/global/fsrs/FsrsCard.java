package com.notwrong.qversity.global.fsrs;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Instant;

@Getter
@Builder
@With
public class FsrsCard {
    @Builder.Default
    private double stability = 0.0;
    @Builder.Default
    private double difficulty = 0.0;
    @Builder.Default
    private double elapsedDays = 0.0;
    @Builder.Default
    private double scheduledDays = 0.0;
    @Builder.Default
    private int reps = 0;
    @Builder.Default
    private int lapses = 0;
    @Builder.Default
    private FsrsState state = FsrsState.NEW;
    private Instant lastReview;
    private Instant due;

    public static FsrsCard newCard(Instant now) {
        return FsrsCard.builder()
                .state(FsrsState.NEW)
                .due(now)
                .build();
    }
}
