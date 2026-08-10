package com.notwrong.qversity.domain.study.entity;

import com.notwrong.qversity.domain.card.entity.CardItem;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.global.fsrs.FsrsState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_schedules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "card_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardItem card;

    @Column(nullable = false)
    @Builder.Default
    private double stability = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private double difficulty = 0.0;

    @Column(name = "elapsed_days")
    @Builder.Default
    private double elapsedDays = 0.0;

    @Column(name = "scheduled_days")
    @Builder.Default
    private double scheduledDays = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private int reps = 0;

    @Column(nullable = false)
    @Builder.Default
    private int lapses = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FsrsState state = FsrsState.NEW;

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "last_review_date")
    private Instant lastReviewDate;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
