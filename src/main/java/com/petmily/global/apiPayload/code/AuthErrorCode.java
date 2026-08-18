package com.petmily.global.apiPayload.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_1", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH401_2", "토큰을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH401_3","유저를 찾을 수 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
