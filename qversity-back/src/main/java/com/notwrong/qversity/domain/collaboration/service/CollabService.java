package com.notwrong.qversity.domain.collaboration.service;

import com.notwrong.qversity.domain.collaboration.dto.CollabSessionResponse;

public interface CollabService {
    CollabSessionResponse createSession(Long userId, Long deckId);
    CollabSessionResponse getSession(String roomCode, Long userId);
    CollabSessionResponse joinSession(String roomCode, Long userId);
    void setReady(String roomCode, Long userId, boolean ready);
    CollabSessionResponse startSession(String roomCode, Long userId);
    void leaveSession(String roomCode, Long userId);
}
