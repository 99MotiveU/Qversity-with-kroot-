package com.notwrong.qversity.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_logs",
        indexes = {
                @Index(name = "idx_social_id", columnList = "social_id"),
                @Index(name = "idx_deletable_after", columnList = "deletable_after")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(name = "social_id", nullable = false)
    private Long socialId;

    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name = "logout_at")
    private LocalDateTime logoutAt;

    @Column(name = "deletable_after")
    private LocalDateTime deletableAfter;
}
