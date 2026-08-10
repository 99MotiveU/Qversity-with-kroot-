package com.notwrong.qversity.domain.study.dto;

import com.notwrong.qversity.domain.card.entity.CardItem;
import com.notwrong.qversity.domain.study.entity.CardSchedule;
import com.notwrong.qversity.global.fsrs.FsrsState;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class StudyCardResponse {
    private Long cardId;
    private String frontContent;
    private String backContent;
    private FsrsState state;
    private int reps;
    private int lapses;
    private double stability;
    private double difficulty;
    private Instant dueDate;

    public static StudyCardResponse from(CardItem card, CardSchedule schedule) {
        return StudyCardResponse.builder()
                .cardId(card.getCardId())
                .frontContent(card.getFrontContent())
                .backContent(card.getBackContent())
                .state(schedule.getState())
                .reps(schedule.getReps())
                .lapses(schedule.getLapses())
                .stability(schedule.getStability())
                .difficulty(schedule.getDifficulty())
                .dueDate(schedule.getDueDate())
                .build();
    }

    public static StudyCardResponse fromNew(CardItem card) {
        return StudyCardResponse.builder()
                .cardId(card.getCardId())
                .frontContent(card.getFrontContent())
                .backContent(card.getBackContent())
                .state(FsrsState.NEW)
                .reps(0)
                .lapses(0)
                .stability(0)
                .difficulty(0)
                .dueDate(Instant.now())
                .build();
    }
}
