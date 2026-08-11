package com.notwrong.qversity.domain.study.dto;

import lombok.Getter;

@Getter
public class ReviewRequest {
    private Long cardId;
    private int rating; // 1=Again, 2=Hard, 3=Good, 4=Easy
}
