package com.notwrong.qversity.domain.user.service;

import com.notwrong.qversity.domain.user.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findBySocialId(Long socialId);
    Optional<User> findByUserId(Long userId);
    void registerUser(String nickname, String providerUniqueId);
    void updateNickname(Long userId, String nickname);
}
