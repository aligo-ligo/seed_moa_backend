package com.intouch.aligooligo.domain.auth.service;

import com.intouch.aligooligo.domain.auth.entity.RefreshToken;
import com.intouch.aligooligo.domain.auth.repository.RefreshTokenRepository;
import com.intouch.aligooligo.global.exception.SocialLoginFailedException;
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
        try {
            refreshTokenRepository.save(new RefreshToken(userEmail, refreshToken));
        } catch (RedisConnectionFailureException e) {
            log.error("RefreshTokenService - saveTokenInfo : redis에 연결할 수 없습니다.");
            throw new SocialLoginFailedException("RefreshTokenService - saveTokenInfo : redis에 연결할 수 없습니다.");
        }
    }
    @Transactional(readOnly = true)
    public RefreshToken findById(String userEmail) {
        return refreshTokenRepository.findById(userEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                            "RefreshTokenService - findById : email을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteById(String userEmail) {
        refreshTokenRepository.deleteById(userEmail);
    }
}
