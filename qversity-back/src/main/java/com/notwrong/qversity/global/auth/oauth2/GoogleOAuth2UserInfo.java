package com.notwrong.qversity.global.auth.oauth2;

import java.util.Map;

/** @deprecated Use {@link com.notwrong.qversity.global.auth.oauth2.provider.GoogleOAuth2UserInfo} */
@Deprecated
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }
}
