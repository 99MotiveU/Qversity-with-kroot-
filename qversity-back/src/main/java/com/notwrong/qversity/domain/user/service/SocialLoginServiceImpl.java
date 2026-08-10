package com.notwrong.qversity.domain.user.service;

import com.notwrong.qversity.domain.user.entity.SocialLogin;
import com.notwrong.qversity.domain.user.repository.SocialLoginRepository;
import com.notwrong.qversity.global.auth.oauth2.OAuth2Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialLoginServiceImpl implements SocialLoginService{

    private final SocialLoginRepository socialLoginRepository;

    @Override
    public Optional<SocialLogin> findBySocialId(Long socialId) {
        return socialLoginRepository.findById(socialId);
    }

    @Override
    public Optional<SocialLogin> findByProviderUniqueId(String providerUniqueId) {
        return socialLoginRepository.findByProviderUniqueId(providerUniqueId);
    }

    @Override
    @Transactional
    public SocialLogin createSocialLogin(String email, OAuth2Provider provider, String providerUniqueId) {

        Optional<SocialLogin> existingSocialLogin = socialLoginRepository.findByProviderUniqueId(providerUniqueId);
        if (existingSocialLogin.isPresent()) {
            return existingSocialLogin.get();
        }

        SocialLogin socialLogin = SocialLogin.builder()
                .email(email)
                .provider(provider)
                .providerUniqueId(providerUniqueId)
                .build();

        return socialLoginRepository.save(socialLogin);
    }
}
