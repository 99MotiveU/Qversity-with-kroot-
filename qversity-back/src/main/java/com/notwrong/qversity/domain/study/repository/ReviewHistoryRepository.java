package com.notwrong.qversity.domain.study.repository;

import com.notwrong.qversity.domain.study.entity.ReviewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewHistoryRepository extends JpaRepository<ReviewHistory, Long> {
    List<ReviewHistory> findByUser_UserIdAndCard_Deck_DeckIdOrderByReviewDateDesc(Long userId, Long deckId);
}
