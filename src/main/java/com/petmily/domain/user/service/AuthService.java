package com.petmily.domain.user.service;

import com.petmily.domain.user.dto.LoginResponse;
import com.petmily.domain.user.entity.User;
import com.petmily.domain.user.repository.UserRepository;
import com.petmily.global.auth.jwt.JwtProvider;
import com.petmily.global.auth.kakao.KakaoClient;
import com.petmily.global.auth.kakao.KakaoTokenResponse;
import com.petmily.global.auth.kakao.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public LoginResponse kakaoLogin(String code) {
        // 1. 인가코드로 카카오 액세스 토큰 발급
        KakaoTokenResponse kakaoToken = kakaoClient.getToken(code);

        // 2. 카카오 액세스 토큰으로 유저 정보 조회
        KakaoUserInfo userInfo = kakaoClient.getUserInfo(kakaoToken.getAccessToken());

        String kakaoId = userInfo.getId();

        // 3. DB에서 유저 조회, 없으면 회원가입
        Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);
        boolean isNewUser = existingUser.isEmpty();

        User user = existingUser.orElseGet(() ->
                userRepository.save(User.createByKakao(
                        kakaoId,
                        userInfo.getNickname(),
                        userInfo.getEmail(),
                        userInfo.getProfileImageUrl()
                ))
        );

        // 4. JWT 발급
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .isNewUser(isNewUser)
                .build();
    }
}
