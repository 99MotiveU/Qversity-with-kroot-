package com.notwrong.qversity.domain.user.service;

import com.notwrong.qversity.domain.user.entity.SocialLogin;
import com.notwrong.qversity.global.auth.oauth2.OAuth2Provider;

import java.util.Optional;

public interface SocialLoginService {

    Optional<SocialLogin> findBySocialId(Long socialId);
    Optional<SocialLogin> findByProviderUniqueId(String providerUniqueId);
    SocialLogin createSocialLogin(String email, OAuth2Provider provider, String providerUniqueId);
}
