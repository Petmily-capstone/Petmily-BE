package com.petmily.domain.product.exception;

import com.petmily.global.apiPayload.code.BaseErrorCode;
import com.petmily.global.exception.BaseException;

public class ProductException extends BaseException {

    public ProductException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
