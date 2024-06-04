package com.intouch.aligooligo.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intouch.aligooligo.domain.auth.entity.RefreshToken;
import com.intouch.aligooligo.domain.auth.repository.RefreshTokenRepository;
import com.intouch.aligooligo.domain.auth.service.RefreshTokenService;
import com.intouch.aligooligo.global.exception.SocialLoginFailedException;
import io.lettuce.core.RedisConnectionException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("리프레시 토큰 저장 테스트")
    void saveTokenInfoTest() {
        //given
        String userEmail = "test@test.com";
        String refreshToken = "refreshToken";
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(new RefreshToken(userEmail, refreshToken));

        //when
        refreshTokenService.saveTokenInfo(userEmail, refreshToken);

        //then
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("리프레시 토큰 저장 예외 테스트")
    void saveTokenInfoTestException() {
        //given
        String userEmail = "test@test.com";
        String refreshToken = "refreshToken";
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenThrow(new SocialLoginFailedException("RefreshTokenService - saveTokenInfo : redis에 연결할 수 없습니다."));

        //when
        final SocialLoginFailedException socialLoginFailedException
                = assertThrows(SocialLoginFailedException.class, () -> refreshTokenService.saveTokenInfo(userEmail, refreshToken));

        //then
        assertThat(socialLoginFailedException.getMessage())
                .isEqualTo("RefreshTokenService - saveTokenInfo : redis에 연결할 수 없습니다.");
    }


    @Test
    @DisplayName("리프레시 토큰 id 기준 찾기")
    void findById() {
        //given
        String userEmail = "test@test.com";
        String refreshToken = "refreshToken";
        when(refreshTokenRepository.findById(any(String.class)))
                .thenReturn(Optional.of(new RefreshToken(userEmail, refreshToken)));

        //when
        RefreshToken resultRefreshToken = refreshTokenService.findById(userEmail);

        //then
        assertThat(resultRefreshToken.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰 id 기준 찾기 예외 테스트")
    void findByIdException() {
        //given
        String userEmail = "test222222222@test.com";
        String refreshToken = "refreshToken";

        //when
        final IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class, () -> refreshTokenService.findById(userEmail));

        //then
        assertThat(illegalArgumentException.getMessage())
                .isEqualTo("RefreshTokenService - findById : email을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 테스트")
    void deleteById() {
        //given
        String userEmail = "test@test.com";

        //when
        refreshTokenService.deleteById(userEmail);

        //then
        verify(refreshTokenRepository, times(1)).deleteById(userEmail);
    }
}
