package com.notwrong.qversity.global.auth.oauth2;

import java.util.Map;

/** @deprecated Use {@link com.notwrong.qversity.global.auth.oauth2.provider.KakaoOAuth2UserInfo} */
@Deprecated
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }
}
