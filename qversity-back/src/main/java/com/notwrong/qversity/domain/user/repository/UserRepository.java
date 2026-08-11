package com.notwrong.qversity.domain.user.repository;

import com.notwrong.qversity.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialLogin_SocialId(Long socialId);
    Optional<User> findByNickname(String nickname);
}
