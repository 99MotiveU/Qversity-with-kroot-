package com.notwrong.qversity.domain.user.service;

import com.notwrong.qversity.domain.user.entity.SocialLogin;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.domain.user.repository.UserRepository;
import com.notwrong.qversity.global.exception.BaseException;
import com.notwrong.qversity.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SocialLoginService socialLoginService;

    @Override
    public Optional<User> findBySocialId(Long socialId) {
        return userRepository.findBySocialLogin_SocialId(socialId);
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public void registerUser(String nickname, String providerUniqueId) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATE_NICKNAME);
        }
        Optional<SocialLogin> socialLoginOptional = socialLoginService.findByProviderUniqueId(providerUniqueId);
        if (socialLoginOptional.isEmpty()) {
            throw new BaseException(ErrorCode.SOCIAL_LOGIN_NOT_FOUND);
        }
        SocialLogin socialLogin = socialLoginOptional.get();
        User user = User.builder()
                .socialLogin(socialLogin)
                .nickname(nickname)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateNickname(Long userId, String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATE_NICKNAME);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        user.setNickname(nickname);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
