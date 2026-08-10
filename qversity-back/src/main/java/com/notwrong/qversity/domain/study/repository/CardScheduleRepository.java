package com.notwrong.qversity.domain.study.repository;

import com.notwrong.qversity.domain.study.entity.CardSchedule;
import com.notwrong.qversity.global.fsrs.FsrsState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CardScheduleRepository extends JpaRepository<CardSchedule, Long> {

    Optional<CardSchedule> findByUser_UserIdAndCard_CardId(Long userId, Long cardId);

    List<CardSchedule> findByUser_UserIdAndCard_Deck_DeckId(Long userId, Long deckId);

    @Query("SELECT cs FROM CardSchedule cs WHERE cs.user.userId = :userId AND cs.card.deck.deckId = :deckId AND cs.dueDate <= :now")
    List<CardSchedule> findDueCards(@Param("userId") Long userId, @Param("deckId") Long deckId, @Param("now") Instant now);

    int countByUser_UserIdAndCard_Deck_DeckIdAndState(Long userId, Long deckId, FsrsState state);

    @Query("SELECT COUNT(cs) FROM CardSchedule cs WHERE cs.user.userId = :userId AND cs.card.deck.deckId = :deckId AND cs.dueDate <= :now AND cs.state <> :newState")
    int countDueReviews(@Param("userId") Long userId, @Param("deckId") Long deckId, @Param("now") Instant now, @Param("newState") FsrsState newState);
}
