package com.notwrong.qversity.domain.study.dto;

import com.notwrong.qversity.global.fsrs.FsrsState;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ReviewResponse {
    private Long cardId;
    private FsrsState state;
    private double stability;
    private double difficulty;
    private double scheduledDays;
    private Instant dueDate;
    private int rating;
}
