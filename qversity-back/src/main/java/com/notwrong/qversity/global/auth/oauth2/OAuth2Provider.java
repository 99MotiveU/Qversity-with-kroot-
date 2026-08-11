package com.notwrong.qversity.global.auth.oauth2;

public enum OAuth2Provider {

    GOOGLE,
    KAKAO,
    NAVER,
    LOCAL;

    public static OAuth2Provider from(String registrationId) {
        try {
            return OAuth2Provider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + registrationId);
        }
    }
}
