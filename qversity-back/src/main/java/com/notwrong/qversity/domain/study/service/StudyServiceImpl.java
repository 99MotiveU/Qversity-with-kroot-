package com.notwrong.qversity.domain.study.service;

import com.notwrong.qversity.domain.card.entity.CardItem;
import com.notwrong.qversity.domain.card.repository.CardItemRepository;
import com.notwrong.qversity.domain.deck.service.DeckService;
import com.notwrong.qversity.domain.study.dto.DeckStatsResponse;
import com.notwrong.qversity.domain.study.dto.ReviewRequest;
import com.notwrong.qversity.domain.study.dto.ReviewResponse;
import com.notwrong.qversity.domain.study.dto.StudyCardResponse;
import com.notwrong.qversity.domain.study.entity.CardSchedule;
import com.notwrong.qversity.domain.study.entity.ReviewHistory;
import com.notwrong.qversity.domain.study.repository.CardScheduleRepository;
import com.notwrong.qversity.domain.study.repository.ReviewHistoryRepository;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.domain.user.repository.UserRepository;
import com.notwrong.qversity.global.exception.BaseException;
import com.notwrong.qversity.global.exception.ErrorCode;
import com.notwrong.qversity.global.fsrs.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final CardScheduleRepository cardScheduleRepository;
    private final ReviewHistoryRepository reviewHistoryRepository;
    private final CardItemRepository cardItemRepository;
    private final UserRepository userRepository;
    private final DeckService deckService;
    private final FsrsAlgorithm fsrsAlgorithm;

    @Override
    public List<StudyCardResponse> getStudySession(Long deckId, Long userId, int limit) {
        deckService.getDeck(deckId, userId); // validate access

        Instant now = Instant.now();
        List<CardItem> allCards = cardItemRepository.findByDeck_DeckId(deckId);

        List<StudyCardResponse> result = new ArrayList<>();

        for (CardItem card : allCards) {
            if (result.size() >= limit) break;

            Optional<CardSchedule> scheduleOpt = cardScheduleRepository
                    .findByUser_UserIdAndCard_CardId(userId, card.getCardId());

            if (scheduleOpt.isEmpty()) {
                result.add(StudyCardResponse.fromNew(card));
            } else {
                CardSchedule schedule = scheduleOpt.get();
                if (schedule.getDueDate() == null || !schedule.getDueDate().isAfter(now)) {
                    result.add(StudyCardResponse.from(card, schedule));
                }
            }
        }

        Collections.shuffle(result);
        return result.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponse submitReview(Long userId, ReviewRequest request) {
        FsrsRating rating = FsrsRating.from(request.getRating());
        CardItem card = cardItemRepository.findById(request.getCardId())
                .orElseThrow(() -> new BaseException(ErrorCode.CARD_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Instant now = Instant.now();

        CardSchedule schedule = cardScheduleRepository
                .findByUser_UserIdAndCard_CardId(userId, card.getCardId())
                .orElseGet(() -> CardSchedule.builder()
                        .user(user)
                        .card(card)
                        .state(FsrsState.NEW)
                        .build());

        FsrsState stateBefore = schedule.getState();

        FsrsCard fsrsCard = FsrsCard.builder()
                .stability(schedule.getStability())
                .difficulty(schedule.getDifficulty())
                .elapsedDays(schedule.getElapsedDays())
                .scheduledDays(schedule.getScheduledDays())
                .reps(schedule.getReps())
                .lapses(schedule.getLapses())
                .state(schedule.getState())
                .lastReview(schedule.getLastReviewDate())
                .due(schedule.getDueDate() != null ? schedule.getDueDate() : now)
                .build();

        Map<FsrsRating, FsrsSchedulingInfo> result = fsrsAlgorithm.repeat(fsrsCard, now);
        FsrsSchedulingInfo schedulingInfo = result.get(rating);
        FsrsCard updatedCard = schedulingInfo.getCard();

        schedule.setStability(updatedCard.getStability());
        schedule.setDifficulty(updatedCard.getDifficulty());
        schedule.setElapsedDays(updatedCard.getElapsedDays());
        schedule.setScheduledDays(updatedCard.getScheduledDays());
        schedule.setReps(updatedCard.getReps());
        schedule.setLapses(updatedCard.getLapses());
        schedule.setState(updatedCard.getState());
        schedule.setLastReviewDate(now);
        schedule.setDueDate(updatedCard.getDue());
        cardScheduleRepository.save(schedule);

        reviewHistoryRepository.save(ReviewHistory.builder()
                .user(user)
                .card(card)
                .rating(rating)
                .reviewDate(now)
                .elapsedDays(updatedCard.getElapsedDays())
                .scheduledDays(updatedCard.getScheduledDays())
                .stateBefore(stateBefore)
                .build());

        return ReviewResponse.builder()
                .cardId(card.getCardId())
                .state(updatedCard.getState())
                .stability(updatedCard.getStability())
                .difficulty(updatedCard.getDifficulty())
                .scheduledDays(updatedCard.getScheduledDays())
                .dueDate(updatedCard.getDue())
                .rating(rating.getValue())
                .build();
    }

    @Override
    public DeckStatsResponse getDeckStats(Long deckId, Long userId) {
        deckService.getDeck(deckId, userId);
        Instant now = Instant.now();

        int total = cardItemRepository.countByDeck_DeckId(deckId);
        int total2 = cardScheduleRepository.findByUser_UserIdAndCard_Deck_DeckId(userId, deckId).size();
        int newCards = total - total2;
        int learning = cardScheduleRepository.countByUser_UserIdAndCard_Deck_DeckIdAndState(userId, deckId, FsrsState.LEARNING);
        int relearning = cardScheduleRepository.countByUser_UserIdAndCard_Deck_DeckIdAndState(userId, deckId, FsrsState.RELEARNING);
        int review = cardScheduleRepository.countByUser_UserIdAndCard_Deck_DeckIdAndState(userId, deckId, FsrsState.REVIEW);
        int due = cardScheduleRepository.countDueReviews(userId, deckId, now, FsrsState.NEW);

        return DeckStatsResponse.builder()
                .deckId(deckId)
                .totalCards(total)
                .newCards(Math.max(0, newCards))
                .learningCards(learning + relearning)
                .reviewCards(review)
                .dueCards(due + Math.max(0, newCards))
                .averageRetention(0.0)
                .build();
    }
}
