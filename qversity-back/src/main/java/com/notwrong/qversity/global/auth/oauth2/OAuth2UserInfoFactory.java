package com.notwrong.qversity.global.auth.oauth2;

import com.notwrong.qversity.global.exception.BaseException;
import com.notwrong.qversity.global.exception.ErrorCode;

import com.notwrong.qversity.global.auth.oauth2.provider.GoogleOAuth2UserInfo;
import com.notwrong.qversity.global.auth.oauth2.provider.KakaoOAuth2UserInfo;
import com.notwrong.qversity.global.auth.oauth2.provider.NaverOAuth2UserInfo;

import java.util.Map;
import java.util.function.Function;

public class OAuth2UserInfoFactory {

    private static final Map<OAuth2Provider, Function<Map<String, Object>, OAuth2UserInfo>> PROVIDERS =
            Map.of(
                    OAuth2Provider.GOOGLE, GoogleOAuth2UserInfo::new,
                    OAuth2Provider.KAKAO, KakaoOAuth2UserInfo::new,
                    OAuth2Provider.NAVER, NaverOAuth2UserInfo::new
            );

    private OAuth2UserInfoFactory() {}

    public static OAuth2UserInfo get(String registrationId, Map<String, Object> attributes) {
        OAuth2Provider provider;
        try {
            provider = OAuth2Provider.from(registrationId);
        } catch (IllegalArgumentException e) {
            throw new BaseException(ErrorCode.COMMON_BAD_REQUEST, e.getMessage());
        }

        Function<Map<String, Object>, OAuth2UserInfo> creator = PROVIDERS.get(provider);

        if (creator == null) {
            throw new BaseException(ErrorCode.COMMON_BAD_REQUEST,
                    "지원하지 않는 OAuth 제공자입니다: " + registrationId);
        }

        return creator.apply(attributes);
    }
}
