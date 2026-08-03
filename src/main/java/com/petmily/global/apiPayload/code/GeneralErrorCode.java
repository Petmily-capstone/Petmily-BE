package com.petmily.global.apiPayload.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON400_1", "요청 값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405_1", "지원하지 않는 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404_1", "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "COMMON409_1", "이미 존재하는 리소스입니다."),
    DATA_INTEGRITY_CONFLICT(HttpStatus.CONFLICT, "COMMON409_2", "데이터 무결성 제약을 위반했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500_1", "서버 내부 오류가 발생했습니다."),

    // External (AI 서버 등 외부 연동)
    EXTERNAL_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "COMMON502_1", "외부 서버 연동에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
