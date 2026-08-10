package com.notwrong.qversity.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access is Denied"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"),

    // User
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "Nickname is already taken"),
    SOCIAL_LOGIN_NOT_FOUND(HttpStatus.NOT_FOUND, "Social login information not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),

    // Deck
    DECK_NOT_FOUND(HttpStatus.NOT_FOUND, "Deck not found"),
    DECK_ACCESS_DENIED(HttpStatus.FORBIDDEN, "No permission to access this deck"),

    // Card
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Card not found"),

    // Collaboration
    COLLAB_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Collaboration session not found"),
    COLLAB_SESSION_FULL(HttpStatus.BAD_REQUEST, "Collaboration session is full"),

    // Local Auth (임시 - 소셜 로그인 전환 시 제거)
    LOCAL_EMAIL_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    LOCAL_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일을 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
