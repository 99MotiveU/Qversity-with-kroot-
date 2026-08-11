package com.notwrong.qversity.global.auth.oauth2;

import com.notwrong.qversity.domain.user.entity.SocialLogin;
import com.notwrong.qversity.domain.user.entity.User;
import com.notwrong.qversity.domain.user.service.SocialLoginService;
import com.notwrong.qversity.domain.user.service.UserLoginLogService;
import com.notwrong.qversity.domain.user.service.UserService;
import com.notwrong.qversity.global.auth.jwt.JwtTokenProvider;
import com.notwrong.qversity.global.common.utils.RandomGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final int TEMP_TOKEN_MAX_AGE = 10 * 60;
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60;
    private static final String TEMP_REGISTRATION_TOKEN_NAME = "tempRegistrationToken";
    private static final String AUTHENTICATED_USER_TOKEN_NAME = "authenticatedUserToken";

    private final SocialLoginService socialLoginService;
    private final UserService userService;
    private final UserLoginLogService userLoginLogService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        clearAllExistingCookies(response);

        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = ((OAuth2User) authentication.getPrincipal()).getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.get(registrationId, attributes);

        if (userInfo == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "사용자 정보를 가져올 수 없습니다.");
            return;
        }

        String email = userInfo.getEmail();
        String providerUniqueId = userInfo.getId();

        Optional<SocialLogin> existingSocialLogin = socialLoginService.findByProviderUniqueId(providerUniqueId);

        if (existingSocialLogin.isPresent()) {
            handleExistingUser(existingSocialLogin.get(), providerUniqueId, registrationId, response);
        } else {
            handleNewUser(email, providerUniqueId, registrationId, response);
        }
    }

    private void handleExistingUser(SocialLogin socialLogin, String providerUniqueId, String registrationId, HttpServletResponse response) throws IOException {
        Optional<User> user = userService.findBySocialId(socialLogin.getSocialId());

        if (user.isPresent()) {
            // Existing user — issue real tokens directly, skip signup
            userLoginLogService.recordLogin(socialLogin.getSocialId());
            String accessToken = jwtTokenProvider.generateAccessToken(
                    user.get().getUserId(), user.get().getNickname(),
                    registrationId, providerUniqueId, socialLogin.getSocialId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(
                    user.get().getUserId(), user.get().getNickname(),
                    registrationId, providerUniqueId, socialLogin.getSocialId());
            setSecureCookie(response, "accessToken", accessToken, 900);
            setRefreshTokenCookie(response, refreshToken);
            response.sendRedirect(frontendUrl + "/");
        } else {
            handleNewUser(socialLogin.getEmail(), providerUniqueId, registrationId, response);
        }
    }

    private void handleNewUser(String email, String providerUniqueId,
                               String registrationId,
                               HttpServletResponse response) throws IOException {

        SocialLogin socialLogin = socialLoginService.createSocialLogin(email,
                OAuth2Provider.valueOf(registrationId.toUpperCase()), providerUniqueId);

        String nickname = RandomGenerator.generateRandomNickname();

        // Register the user with the generated nickname
        userService.registerUser(nickname, providerUniqueId);

        // Proceed to login the newly registered user
        processUserLogin(userService.findBySocialId(socialLogin.getSocialId()).get().getUserId(),
                nickname,
                providerUniqueId,
                registrationId,
                socialLogin.getSocialId(),
                response);
    }

    private void processUserLogin(Long userId, String nickname, String providerUniqueId,
                                  String registrationId, Long socialId,
                                  HttpServletResponse response) throws IOException {
        userLoginLogService.recordLogin(socialId);

        var socialLoginOpt = socialLoginService.findBySocialId(socialId);
        if (socialLoginOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "소셜 로그인 정보를 찾을 수 없습니다.");
            return;
        }

        var socialLogin = socialLoginOpt.get();

        String authenticatedUserToken = jwtTokenProvider.generateAuthenticatedUserToken(
                socialId, socialLogin.getEmail(), registrationId, providerUniqueId,
                userId, nickname
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(
                userId, nickname,
                registrationId, providerUniqueId, socialId);

        setSecureCookie(response, AUTHENTICATED_USER_TOKEN_NAME, authenticatedUserToken, TEMP_TOKEN_MAX_AGE);
        setRefreshTokenCookie(response, refreshToken);

        response.sendRedirect(frontendUrl + "/");

    }

    private void clearAllExistingCookies(HttpServletResponse response) {
        String[] cookiesToClear = {"accessToken", "refreshToken"};

        for (String cookieName : cookiesToClear) {
            Cookie cookie = new Cookie(cookieName, "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);
        }
    }

    private void setSecureCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // authenticatedUserToken은 JS에서 읽어야 하므로 HttpOnly 제외
        boolean httpOnly = !AUTHENTICATED_USER_TOKEN_NAME.equals(name);
        String httpOnlyFlag = httpOnly ? "; HttpOnly" : "";
        String cookieValue = String.format(
                "%s=%s; Path=/%s; SameSite=Lax; Max-Age=%d",
                name, value, httpOnlyFlag, maxAge
        );
        response.addHeader("Set-Cookie", cookieValue);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        setSecureCookie(response, "refreshToken", refreshToken, REFRESH_TOKEN_MAX_AGE);
    }

    private void handleIncompleteRegistration(SocialLogin socialLogin, String providerUniqueId,
                                              String registrationId,
                                              HttpServletResponse response) throws IOException {

        String tempRegistrationToken = jwtTokenProvider.generateTempRegistrationToken(
                socialLogin.getSocialId(),
                socialLogin.getEmail(),
                registrationId,
                providerUniqueId
        );

        setSecureCookie(response, TEMP_REGISTRATION_TOKEN_NAME, tempRegistrationToken, TEMP_TOKEN_MAX_AGE);
        response.sendRedirect(frontendUrl + "/");
    }
}
