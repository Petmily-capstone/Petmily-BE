package com.petmily.domain.user.controller;

import com.petmily.domain.user.dto.request.ReissueRequest;
import com.petmily.domain.user.dto.response.LoginResponse;
import com.petmily.domain.user.repository.RefreshTokenRepository;
import com.petmily.domain.user.service.AuthService;
import com.petmily.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/reissue")
    public ApiResponse<LoginResponse> reissue(@RequestBody ReissueRequest request) {
        LoginResponse response = authService.reissue(request.getRefreshToken());
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ApiResponse.onSuccess();
    }
}
