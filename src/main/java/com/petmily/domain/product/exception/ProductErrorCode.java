package com.petmily.domain.product.exception;

import com.petmily.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT404_1", "상품을 찾을 수 없습니다."),
    INVALID_PRICE_RANGE(HttpStatus.BAD_REQUEST, "PRODUCT400_1", "가격 범위가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
