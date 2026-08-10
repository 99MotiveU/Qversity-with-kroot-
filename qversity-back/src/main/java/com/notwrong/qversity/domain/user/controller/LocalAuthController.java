package com.notwrong.qversity.domain.user.controller;

import com.notwrong.qversity.domain.user.dto.UserResponse;
import com.notwrong.qversity.domain.user.entity.LocalCredential;
import com.notwrong.qversity.domain.user.entity.SocialLogin;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.domain.user.repository.LocalCredentialRepository;
import com.notwrong.qversity.domain.user.repository.UserRepository;
import com.notwrong.qversity.domain.user.service.SocialLoginService;
import com.notwrong.qversity.domain.user.service.UserService;
import com.notwrong.qversity.global.auth.oauth2.OAuth2Provider;
import com.notwrong.qversity.global.exception.BaseException;
import com.notwrong.qversity.global.exception.ErrorCode;
import com.notwrong.qversity.global.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

// 임시 일반 로그인 컨트롤러 — 소셜 로그인 전환 시 이 파일 전체 삭제
@RestController
@RequestMapping("/api/auth/local")
@RequiredArgsConstructor
public class LocalAuthController {

    private final LocalCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final SocialLoginService socialLoginService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, String> body,
            HttpServletResponse response) {

        String email = body.get("email");
        String password = body.get("password");
        String nickname = body.get("nickname");

        if (email == null || email.isBlank() || password == null || password.isBlank()
                || nickname == null || nickname.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "이메일, 비밀번호, 닉네임을 모두 입력해주세요."));
        }
        if (credentialRepository.existsByEmail(email)) {
            throw new BaseException(ErrorCode.LOCAL_EMAIL_EXISTS);
        }
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String providerUniqueId = "local:" + email;
        SocialLogin socialLogin = socialLoginService.createSocialLogin(email, OAuth2Provider.LOCAL, providerUniqueId);

        User user = User.builder()
                .socialLogin(socialLogin)
                .nickname(nickname)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        LocalCredential credential = LocalCredential.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .user(user)
                .build();
        credentialRepository.save(credential);

        issueTokenCookies(response, user, socialLogin, "local", providerUniqueId);
        return ResponseEntity.ok(Map.of("success", true, "data", UserResponse.from(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body,
            HttpServletResponse response) {

        String email = body.get("email");
        String password = body.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "이메일과 비밀번호를 입력해주세요."));
        }

        LocalCredential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorCode.LOCAL_USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, credential.getPasswordHash())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        User user = credential.getUser();
        SocialLogin socialLogin = user.getSocialLogin();

        issueTokenCookies(response, user, socialLogin, "local", "local:" + email);
        return ResponseEntity.ok(Map.of("success", true, "data", UserResponse.from(user)));
    }

    private void issueTokenCookies(HttpServletResponse response, User user,
                                   SocialLogin socialLogin, String provider, String providerUniqueId) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getUserId(), user.getNickname(), provider, providerUniqueId, socialLogin.getSocialId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getUserId(), user.getNickname(), provider, providerUniqueId, socialLogin.getSocialId());

        addCookie(response, "accessToken", accessToken, 900);
        addCookie(response, "refreshToken", refreshToken, 604800);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        response.addHeader("Set-Cookie",
                String.format("%s=%s; Path=/; HttpOnly; SameSite=Lax; Max-Age=%d", name, value, maxAge));
    }
}
