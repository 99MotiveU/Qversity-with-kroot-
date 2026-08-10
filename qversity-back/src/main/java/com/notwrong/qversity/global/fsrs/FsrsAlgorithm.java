package com.notwrong.qversity.global.fsrs;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.Map;

@Component
public class FsrsAlgorithm {

    // FSRS-5 default weights
    private static final double[] W = {
            0.4072, 1.1829, 3.1262, 15.4722,
            7.2102, 0.5316, 1.0651, 0.0589,
            1.5330, 0.1544, 1.0045, 1.9935,
            0.1220, 0.2900, 2.2700, 0.2900,
            2.9898, 0.5100, 0.3400
    };

    private static final double DECAY = -0.5;
    private static final double FACTOR = Math.pow(0.9, 1.0 / DECAY) - 1; // ~0.2342
    private static final double REQUEST_RETENTION = 0.9;
    private static final double MAXIMUM_INTERVAL = 36500;

    // Short-term scheduling intervals in minutes
    private static final int[] LEARNING_STEPS = {1, 10};
    private static final int[] RELEARNING_STEPS = {10};

    /**
     * Schedule a card for all 4 ratings and return the results.
     */
    public Map<FsrsRating, FsrsSchedulingInfo> repeat(FsrsCard card, Instant now) {
        Map<FsrsRating, FsrsSchedulingInfo> result = new EnumMap<>(FsrsRating.class);

        double elapsedDays = 0;
        if (card.getLastReview() != null) {
            elapsedDays = Math.max(0, ChronoUnit.DAYS.between(card.getLastReview(), now));
        }

        for (FsrsRating rating : FsrsRating.values()) {
            FsrsCard scheduled = schedule(card, rating, now, elapsedDays);
            result.put(rating, FsrsSchedulingInfo.builder()
                    .card(scheduled)
                    .rating(rating)
                    .reviewDate(now)
                    .build());
        }
        return result;
    }

    private FsrsCard schedule(FsrsCard card, FsrsRating rating, Instant now, double elapsedDays) {
        return switch (card.getState()) {
            case NEW -> scheduleNew(card, rating, now);
            case LEARNING, RELEARNING -> scheduleLearning(card, rating, now, elapsedDays);
            case REVIEW -> scheduleReview(card, rating, now, elapsedDays);
        };
    }

    private FsrsCard scheduleNew(FsrsCard card, FsrsRating rating, Instant now) {
        double stability = initStability(rating.getValue());
        double difficulty = constrainDifficulty(initDifficulty(rating.getValue()));
        int reps = 1;
        int lapses = card.getLapses();

        FsrsState nextState;
        Instant due;
        double scheduledDays;

        if (rating == FsrsRating.AGAIN || rating == FsrsRating.HARD) {
            nextState = FsrsState.LEARNING;
            scheduledDays = 0;
            due = now.plus(LEARNING_STEPS[0], ChronoUnit.MINUTES);
        } else if (rating == FsrsRating.GOOD) {
            nextState = FsrsState.LEARNING;
            int minutes = LEARNING_STEPS.length > 1 ? LEARNING_STEPS[1] : LEARNING_STEPS[0];
            scheduledDays = 0;
            due = now.plus(minutes, ChronoUnit.MINUTES);
        } else { // EASY
            nextState = FsrsState.REVIEW;
            scheduledDays = nextInterval(stability);
            due = now.plus((long) scheduledDays, ChronoUnit.DAYS);
        }

        return card.withStability(stability)
                .withDifficulty(difficulty)
                .withReps(reps)
                .withLapses(lapses)
                .withState(nextState)
                .withElapsedDays(0)
                .withScheduledDays(scheduledDays)
                .withLastReview(now)
                .withDue(due);
    }

    private FsrsCard scheduleLearning(FsrsCard card, FsrsRating rating, Instant now, double elapsedDays) {
        double stability = card.getStability();
        double difficulty = constrainDifficulty(nextDifficulty(card.getDifficulty(), rating.getValue()));
        int reps = card.getReps() + 1;
        int lapses = card.getLapses();

        boolean isRelearning = card.getState() == FsrsState.RELEARNING;
        int[] steps = isRelearning ? RELEARNING_STEPS : LEARNING_STEPS;

        FsrsState nextState;
        Instant due;
        double scheduledDays;

        if (rating == FsrsRating.AGAIN) {
            nextState = isRelearning ? FsrsState.RELEARNING : FsrsState.LEARNING;
            scheduledDays = 0;
            due = now.plus(steps[0], ChronoUnit.MINUTES);
            stability = Math.max(0.1, stability * 0.5);
        } else if (rating == FsrsRating.GOOD) {
            if (steps.length > 1) {
                nextState = card.getState();
                scheduledDays = 0;
                due = now.plus(steps[steps.length - 1], ChronoUnit.MINUTES);
            } else {
                nextState = FsrsState.REVIEW;
                stability = nextShortTermStability(stability, rating.getValue());
                scheduledDays = nextInterval(stability);
                due = now.plus((long) Math.max(1, scheduledDays), ChronoUnit.DAYS);
            }
        } else if (rating == FsrsRating.EASY) {
            nextState = FsrsState.REVIEW;
            stability = nextShortTermStability(stability, rating.getValue());
            scheduledDays = nextInterval(stability);
            due = now.plus((long) Math.max(1, scheduledDays), ChronoUnit.DAYS);
        } else { // HARD
            nextState = card.getState();
            scheduledDays = 0;
            due = now.plus(steps[0], ChronoUnit.MINUTES);
        }

        return card.withStability(stability)
                .withDifficulty(difficulty)
                .withReps(reps)
                .withLapses(lapses)
                .withState(nextState)
                .withElapsedDays(elapsedDays)
                .withScheduledDays(scheduledDays)
                .withLastReview(now)
                .withDue(due);
    }

    private FsrsCard scheduleReview(FsrsCard card, FsrsRating rating, Instant now, double elapsedDays) {
        double retrievability = forgettingCurve(elapsedDays, card.getStability());
        double difficulty = constrainDifficulty(nextDifficulty(card.getDifficulty(), rating.getValue()));
        int reps = card.getReps() + 1;
        int lapses = card.getLapses();

        double stability;
        FsrsState nextState;
        Instant due;
        double scheduledDays;

        if (rating == FsrsRating.AGAIN) {
            lapses++;
            stability = nextStabilityAfterForgetting(card.getDifficulty(), card.getStability(), retrievability);
            nextState = FsrsState.RELEARNING;
            scheduledDays = 0;
            due = now.plus(RELEARNING_STEPS[0], ChronoUnit.MINUTES);
        } else {
            stability = nextStabilityAfterRecall(card.getDifficulty(), card.getStability(), retrievability, rating.getValue());
            nextState = FsrsState.REVIEW;
            scheduledDays = nextInterval(stability);
            due = now.plus((long) Math.max(1, scheduledDays), ChronoUnit.DAYS);
        }

        return card.withStability(stability)
                .withDifficulty(difficulty)
                .withReps(reps)
                .withLapses(lapses)
                .withState(nextState)
                .withElapsedDays(elapsedDays)
                .withScheduledDays(scheduledDays)
                .withLastReview(now)
                .withDue(due);
    }

    private double initStability(int rating) {
        return Math.max(W[rating - 1], 0.1);
    }

    private double initDifficulty(int rating) {
        return W[4] - Math.exp(W[5] * (rating - 1)) + 1;
    }

    private double nextDifficulty(double d, int rating) {
        double next = d - W[6] * (rating - 3);
        return meanReversion(initDifficulty(4), next);
    }

    private double meanReversion(double init, double curr) {
        return W[7] * init + (1 - W[7]) * curr;
    }

    private double constrainDifficulty(double d) {
        return Math.min(Math.max(d, 1.0), 10.0);
    }

    public double forgettingCurve(double elapsedDays, double stability) {
        if (stability <= 0) return 0;
        return Math.pow(1 + FACTOR * elapsedDays / stability, DECAY);
    }

    private double nextStabilityAfterRecall(double d, double s, double r, int rating) {
        double hardPenalty = (rating == 2) ? W[15] : 1.0;
        double easyBonus = (rating == 4) ? W[16] : 1.0;
        return s * (Math.exp(W[8]) * (11 - d) * Math.pow(s, -W[9])
                * (Math.exp(W[10] * (1 - r)) - 1) * hardPenalty * easyBonus + 1);
    }

    private double nextStabilityAfterForgetting(double d, double s, double r) {
        return W[11] * Math.pow(d, -W[12]) * (Math.pow(s + 1, W[13]) - 1) * Math.exp(W[14] * (1 - r));
    }

    private double nextShortTermStability(double s, int rating) {
        return s * Math.exp(W[17] * (rating - 3 + W[18]));
    }

    private double nextInterval(double stability) {
        double interval = stability / FACTOR * (Math.pow(REQUEST_RETENTION, 1.0 / DECAY) - 1);
        return Math.min(Math.max(Math.round(interval), 1), MAXIMUM_INTERVAL);
    }
}
