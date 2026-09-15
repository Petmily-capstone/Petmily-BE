package com.petmily.domain.user.service;

import com.petmily.domain.user.dto.response.LoginResponse;
import com.petmily.domain.user.entity.RefreshToken;
import com.petmily.domain.user.entity.User;
import com.petmily.domain.user.repository.RefreshTokenRepository;
import com.petmily.domain.user.repository.UserRepository;
import com.petmily.global.auth.jwt.JwtProvider;
import com.petmily.global.auth.kakao.KakaoClient;
import com.petmily.global.auth.kakao.KakaoTokenResponse;
import com.petmily.global.auth.kakao.KakaoUserInfo;
import com.petmily.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.petmily.global.apiPayload.code.AuthErrorCode.INVALID_TOKEN;
import static com.petmily.global.apiPayload.code.AuthErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        if (existingToken.isPresent()) {
            RefreshToken token = existingToken.get();
            token.rotate(refreshToken, LocalDateTime.now().plusDays(14));
            refreshTokenRepository.save(token);
        } else {
            RefreshToken token = RefreshToken.builder()
                    .user(user)
                    .newToken(refreshToken)
                    .newExpireAt(LocalDateTime.now().plusDays(14))
                    .build();
            refreshTokenRepository.save(token);
        }


        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .isNewUser(isNewUser)
                .build();
    }

    public LoginResponse reissue(String refreshToken) {
        if (!jwtProvider.isValid(refreshToken)) {
            throw new BaseException(INVALID_TOKEN);

        }
        if (refreshTokenRepository.findByToken(refreshToken).isEmpty()) {
            throw new BaseException(INVALID_TOKEN);
        }
        Long userId = jwtProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId).orElseThrow(()
                -> new BaseException(USER_NOT_FOUND));


        String newAccessToken = jwtProvider.createAccessToken(user.getId());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        if (existingToken.isPresent()) {
            RefreshToken token = existingToken.get();
            token.rotate(newRefreshToken, LocalDateTime.now().plusDays(14));
            refreshTokenRepository.save(token);
        } else {
            RefreshToken token = RefreshToken.builder()
                    .user(user)
                    .newToken(newRefreshToken)
                    .newExpireAt(LocalDateTime.now().plusDays(14))
                    .build();
            refreshTokenRepository.save(token);


        }

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .isNewUser(false)
                .build();

    }
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
        refreshTokenRepository.deleteByUser(user);
    }
}