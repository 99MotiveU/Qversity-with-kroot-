package com.notwrong.qversity.domain.study.service;

import com.notwrong.qversity.domain.study.dto.DeckStatsResponse;
import com.notwrong.qversity.domain.study.dto.ReviewRequest;
import com.notwrong.qversity.domain.study.dto.ReviewResponse;
import com.notwrong.qversity.domain.study.dto.StudyCardResponse;

import java.util.List;

public interface StudyService {
    List<StudyCardResponse> getStudySession(Long deckId, Long userId, int limit);
    ReviewResponse submitReview(Long userId, ReviewRequest request);
    DeckStatsResponse getDeckStats(Long deckId, Long userId);
}
