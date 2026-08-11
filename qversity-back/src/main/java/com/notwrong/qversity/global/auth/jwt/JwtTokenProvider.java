package com.notwrong.qversity.global.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class JwtTokenProvider {

    private static final long TEMP_TOKEN_EXPIRATION = 10 * 60 * 1000L;
    private static final String AUTHENTICATED_USER_SUBJECT = "authenticated_user";
    private static final String TEMP_REGISTRATION_SUBJECT = "temp_registration";
    private static final String USER_ID = "bifId";
    private static final String NICKNAME = "nickname";
    private static final String PROVIDER = "provider";
    private static final String SOCIAL_ID = "socialId";
    private static final String EMAIL = "email";
    private static final String PROVIDER_UNIQUE_ID = "providerUniqueId";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(Long bifId, String nickname,
                                      String provider, String providerUniqueId, Long socialId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(providerUniqueId)
                .claim(USER_ID, bifId)
                .claim(NICKNAME, nickname)
                .claim(PROVIDER, provider)
                .claim(SOCIAL_ID, socialId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long bifId, String nickname,
                                       String provider, String providerUniqueId, Long socialId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(providerUniqueId)
                .claim("type", "refresh")
                .claim(USER_ID, bifId)
                .claim(NICKNAME, nickname)
                .claim(PROVIDER, provider)
                .claim(SOCIAL_ID, socialId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateAuthenticatedUserToken(Long socialId, String email, String provider,
                                                 String providerUniqueId,
                                                 Long bifId, String nickname) {
        return generateTokenWithClaims(AUTHENTICATED_USER_SUBJECT, TEMP_TOKEN_EXPIRATION, claims -> {
            claims.put(SOCIAL_ID, socialId);
            claims.put(EMAIL, email);
            claims.put(PROVIDER, provider);
            claims.put(PROVIDER_UNIQUE_ID, providerUniqueId);
            claims.put(USER_ID, bifId);
            claims.put(NICKNAME, nickname);
        });
    }

    public String generateTempRegistrationToken(Long socialId, String email, String provider, String providerUniqueId) {
        return generateTokenWithClaims(TEMP_REGISTRATION_SUBJECT, TEMP_TOKEN_EXPIRATION, claims -> {
            claims.put(SOCIAL_ID, socialId);
            claims.put(EMAIL, email);
            claims.put(PROVIDER, provider);
            claims.put(PROVIDER_UNIQUE_ID, providerUniqueId);
        });
    }

    private String generateTokenWithClaims(String subject, long expiration, Consumer<Map<String, Object>> claimsBuilder) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claimsBuilder.accept(claims);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return "SUCCESS";
        } catch (ExpiredJwtException e) {
            return "EXPIRED_TOKEN";
        } catch (JwtException e) {
            return "INVALID_TOKEN";
        } catch (Exception e) {
            return "SERVER_ERROR";
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String subject = claims.getSubject();

        if (TEMP_REGISTRATION_SUBJECT.equals(subject)){
            return null;
        }
        return claims.get(USER_ID, Long.class);
    }

    public String getNicknameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String subject = claims.getSubject();

        if (TEMP_REGISTRATION_SUBJECT.equals(subject)){
            return null;
        }
        return claims.get(NICKNAME, String.class);
    }

    public String getProviderFromToken(String token) {
        return getClaimsFromToken(token).get(PROVIDER, String.class);
    }

    public Long getSocialIdFromToken(String token) {
        return getClaimsFromToken(token).get(SOCIAL_ID, Long.class);
    }

    public String getProviderUniqueIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String subject = claims.getSubject();

        if (TEMP_REGISTRATION_SUBJECT.equals(subject) || AUTHENTICATED_USER_SUBJECT.equals(subject)){
            return claims.get(PROVIDER_UNIQUE_ID, String.class);
        } else {
            return subject;
        }
    }
}
