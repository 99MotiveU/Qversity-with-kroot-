package com.notwrong.qversity.domain.collaboration.repository;

import com.notwrong.qversity.domain.collaboration.entity.CollabMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollabMemberRepository extends JpaRepository<CollabMember, Long> {
    List<CollabMember> findBySession_Id(Long sessionId);
    Optional<CollabMember> findBySession_IdAndUser_UserId(Long sessionId, Long userId);
    int countBySession_Id(Long sessionId);
}
