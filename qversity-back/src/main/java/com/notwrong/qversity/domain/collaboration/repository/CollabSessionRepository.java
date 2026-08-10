package com.notwrong.qversity.domain.collaboration.repository;

import com.notwrong.qversity.domain.collaboration.entity.CollabSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollabSessionRepository extends JpaRepository<CollabSession, Long> {
    Optional<CollabSession> findByRoomCode(String roomCode);
}
