package com.notwrong.qversity.domain.user.controller;

import com.notwrong.qversity.domain.user.dto.UserResponse;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.domain.user.service.UserService;
import com.notwrong.qversity.global.auth.dto.CustomUserDetails;
import com.notwrong.qversity.global.auth.jwt.JwtTokenProvider;
import com.notwrong.qversity.global.exception.BaseException;
import com.notwrong.qversity.global.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", UserResponse.from(user)
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody Map<String, String> body) {
        String nickname = body.get("nickname");
        if (nickname == null || nickname.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "닉네임이 필요합니다."));
        }

        String token = null;
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(c -> "authenticatedUserToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst().orElse(null);
        }
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "인증 토큰이 없습니다."));
        }

        String providerUniqueId = jwtTokenProvider.getProviderUniqueIdFromToken(token);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String provider = jwtTokenProvider.getProviderFromToken(token);
        Long socialId = jwtTokenProvider.getSocialIdFromToken(token);

        if (userId != null) {
            userService.updateNickname(userId, nickname);
        } else {
            userService.registerUser(nickname, providerUniqueId);
        }

        User user = userService.findBySocialId(socialId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getUserId(), user.getNickname(), provider, providerUniqueId, socialId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getUserId(), user.getNickname(), provider, providerUniqueId, socialId);

        clearCookie(response, "authenticatedUserToken");
        setAuthCookie(response, "accessToken", accessToken, 900);
        setAuthCookie(response, "refreshToken", refreshToken, 604800);

        return ResponseEntity.ok(Map.of("success", true, "data", UserResponse.from(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        clearCookie(response, "accessToken");
        clearCookie(response, "refreshToken");
        clearCookie(response, "authenticatedUserToken");
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            refreshToken = Arrays.stream(request.getCookies())
                    .filter(c -> "refreshToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst().orElse(null);
        }
        if (refreshToken == null || !"SUCCESS".equals(jwtTokenProvider.validateToken(refreshToken))) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid refresh token"));
        }
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String nickname = jwtTokenProvider.getNicknameFromToken(refreshToken);
        String provider = jwtTokenProvider.getProviderFromToken(refreshToken);
        String providerUniqueId = jwtTokenProvider.getProviderUniqueIdFromToken(refreshToken);
        Long socialId = jwtTokenProvider.getSocialIdFromToken(refreshToken);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, nickname, provider, providerUniqueId, socialId);
        setAuthCookie(response, "accessToken", newAccessToken, 900);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<Map<String, Object>> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> body) {
        String nickname = body.get("nickname");
        if (nickname == null || nickname.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "닉네임이 필요합니다."));
        }
        userService.updateNickname(userDetails.getUserId(), nickname);
        return ResponseEntity.ok(Map.of("success", true));
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private void setAuthCookie(HttpServletResponse response, String name, String value, int maxAge) {
        String cookieValue = String.format("%s=%s; Path=/; HttpOnly; SameSite=Lax; Max-Age=%d", name, value, maxAge);
        response.addHeader("Set-Cookie", cookieValue);
    }
}
