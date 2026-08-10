package com.notwrong.qversity.domain.study.entity;

import com.notwrong.qversity.domain.card.entity.CardItem;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.global.fsrs.FsrsRating;
import com.notwrong.qversity.global.fsrs.FsrsState;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "review_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardItem card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FsrsRating rating;

    @Column(name = "review_date", nullable = false)
    private Instant reviewDate;

    @Column(name = "elapsed_days")
    private double elapsedDays;

    @Column(name = "scheduled_days")
    private double scheduledDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_before", length = 20)
    private FsrsState stateBefore;
}
