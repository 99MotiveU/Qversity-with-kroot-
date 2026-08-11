package com.notwrong.qversity.domain.user.repository;

import com.notwrong.qversity.domain.user.entity.LocalCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 임시 일반 로그인용 레포지토리 — 소셜 로그인 전환 시 삭제
public interface LocalCredentialRepository extends JpaRepository<LocalCredential, Long> {
    Optional<LocalCredential> findByEmail(String email);
    boolean existsByEmail(String email);
}
