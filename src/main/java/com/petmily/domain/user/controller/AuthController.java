package com.petmily.domain.user.controller;

import com.petmily.domain.user.dto.LoginResponse;
import com.petmily.domain.user.service.AuthService;
import com.petmily.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public ApiResponse<LoginResponse> kakaoLogin(@RequestParam String code) {
        LoginResponse response = authService.kakaoLogin(code);
        return ApiResponse.onSuccess(response);
    }
}
