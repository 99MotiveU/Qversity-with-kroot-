package com.notwrong.qversity.domain.user.entity;

import com.notwrong.qversity.global.auth.oauth2.OAuth2Provider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_login")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_id")
    private Long socialId;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "provider", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    @Column(name = "provider_unique_id", nullable = false, unique = true, length = 255)
    private String providerUniqueId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
