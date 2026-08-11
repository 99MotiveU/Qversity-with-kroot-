package com.notwrong.qversity.domain.user.repository;

import com.notwrong.qversity.domain.user.entity.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {

    Optional<SocialLogin> findByProviderUniqueId(String providerUniqueId);
}
