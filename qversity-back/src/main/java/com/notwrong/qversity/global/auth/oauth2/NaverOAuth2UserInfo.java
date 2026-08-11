package com.notwrong.qversity.global.auth.oauth2;

import java.util.Map;

/** @deprecated Use {@link com.notwrong.qversity.global.auth.oauth2.provider.NaverOAuth2UserInfo} */
@Deprecated
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) return null;
        return (String) response.get("email");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) return null;
        return (String) response.get("id");
    }
}
