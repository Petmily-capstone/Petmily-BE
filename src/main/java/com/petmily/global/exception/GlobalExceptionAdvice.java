package com.petmily.global.exception;

import com.petmily.global.apiPayload.ApiResponse;
import com.petmily.global.apiPayload.code.BaseErrorCode;
import com.petmily.global.apiPayload.code.GeneralErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    // 비즈니스 예외
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        log.warn("BaseException: {}", e.getMessage());
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode));
    }

    // @RequestBody 검증 실패 (프레임워크 예외 override)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE,
                        createValidationMessage(ex.getBindingResult())));
    }

    // @RequestParam / @PathVariable 검증 실패
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity.status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE, e.getMessage()));
    }

    // DB 무결성 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolation: {}", e.getMessage());
        return ResponseEntity.status(GeneralErrorCode.DATA_INTEGRITY_CONFLICT.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.DATA_INTEGRITY_CONFLICT));
    }

    // 최종 fallback
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception", e);
        return ResponseEntity.status(GeneralErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.INTERNAL_SERVER_ERROR));
    }

    // 필드별 "field: message" 를 콤마로 join
    private String createValidationMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}
