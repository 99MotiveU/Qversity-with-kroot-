package com.notwrong.qversity.domain.user.dto;

import com.notwrong.qversity.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long userId;
    private String nickname;
    private String email;
    private String provider;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getSocialLogin().getEmail())
                .provider(user.getSocialLogin().getProvider().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
