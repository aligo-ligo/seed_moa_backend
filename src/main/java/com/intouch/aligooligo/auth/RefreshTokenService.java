package com.intouch.aligooligo.auth;

import com.intouch.aligooligo.exception.SocialLoginFailedException;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveTokenInfo(String userEmail, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(userEmail, refreshToken));
    }
    @Transactional(readOnly = true)
    public RefreshToken findById(String userEmail) {
        return refreshTokenRepository.findById(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("db에 리프레시 토큰이 없습니다."));
    }

    @Transactional
    public void deleteById(String userEmail) {
        refreshTokenRepository.deleteById(userEmail);
    }
}
