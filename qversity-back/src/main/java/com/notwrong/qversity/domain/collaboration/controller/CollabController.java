package com.notwrong.qversity.domain.collaboration.controller;

import com.notwrong.qversity.domain.collaboration.dto.CollabSessionResponse;
import com.notwrong.qversity.domain.collaboration.service.CollabService;
import com.notwrong.qversity.global.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/collab")
@RequiredArgsConstructor
public class CollabController {

    private final CollabService collabService;

    @PostMapping("/sessions")
    public ResponseEntity<Map<String, Object>> createSession(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody Map<String, Long> body) {
        Long deckId = body.get("deckId");
        CollabSessionResponse session = collabService.createSession(user.getUserId(), deckId);
        return ResponseEntity.ok(Map.of("success", true, "data", session));
    }

    @GetMapping("/sessions/{roomCode}")
    public ResponseEntity<Map<String, Object>> getSession(
            @PathVariable String roomCode,
            @AuthenticationPrincipal CustomUserDetails user) {
        CollabSessionResponse session = collabService.getSession(roomCode, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true, "data", session));
    }

    @PostMapping("/sessions/{roomCode}/join")
    public ResponseEntity<Map<String, Object>> joinSession(
            @PathVariable String roomCode,
            @AuthenticationPrincipal CustomUserDetails user) {
        CollabSessionResponse session = collabService.joinSession(roomCode, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true, "data", session));
    }

    @PostMapping("/sessions/{roomCode}/ready")
    public ResponseEntity<Map<String, Object>> setReady(
            @PathVariable String roomCode,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody Map<String, Boolean> body) {
        boolean ready = Boolean.TRUE.equals(body.get("ready"));
        collabService.setReady(roomCode, user.getUserId(), ready);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/sessions/{roomCode}/start")
    public ResponseEntity<Map<String, Object>> startSession(
            @PathVariable String roomCode,
            @AuthenticationPrincipal CustomUserDetails user) {
        CollabSessionResponse session = collabService.startSession(roomCode, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true, "data", session));
    }

    @PostMapping("/sessions/{roomCode}/leave")
    public ResponseEntity<Map<String, Object>> leaveSession(
            @PathVariable String roomCode,
            @AuthenticationPrincipal CustomUserDetails user) {
        collabService.leaveSession(roomCode, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
