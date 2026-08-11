package com.notwrong.qversity.domain.study.controller;

import com.notwrong.qversity.domain.study.dto.DeckStatsResponse;
import com.notwrong.qversity.domain.study.dto.ReviewRequest;
import com.notwrong.qversity.domain.study.dto.ReviewResponse;
import com.notwrong.qversity.domain.study.dto.StudyCardResponse;
import com.notwrong.qversity.domain.study.service.StudyService;
import com.notwrong.qversity.global.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/{deckId}/session")
    public ResponseEntity<Map<String, Object>> getStudySession(
            @PathVariable Long deckId,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal CustomUserDetails user) {
        List<StudyCardResponse> cards = studyService.getStudySession(deckId, user.getUserId(), limit);
        return ResponseEntity.ok(Map.of("success", true, "data", cards));
    }

    @PostMapping("/review")
    public ResponseEntity<Map<String, Object>> submitReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ReviewRequest request) {
        ReviewResponse response = studyService.submitReview(user.getUserId(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @GetMapping("/{deckId}/stats")
    public ResponseEntity<Map<String, Object>> getDeckStats(
            @PathVariable Long deckId,
            @AuthenticationPrincipal CustomUserDetails user) {
        DeckStatsResponse stats = studyService.getDeckStats(deckId, user.getUserId());
        return ResponseEntity.ok(Map.of("success", true, "data", stats));
    }
}
