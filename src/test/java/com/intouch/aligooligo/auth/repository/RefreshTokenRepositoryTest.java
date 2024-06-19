//package com.intouch.aligooligo.auth.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import com.intouch.aligooligo.config.RedisTestContainer;
//import com.intouch.aligooligo.domain.auth.entity.RefreshToken;
//import com.intouch.aligooligo.domain.auth.repository.RefreshTokenRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//@ExtendWith(RedisTestContainer.class)
//public class RefreshTokenRepositoryTest {
//    @Autowired
//    RefreshTokenRepository refreshTokenRepository;
//
//    @Test
//    @DisplayName("리프레시 토큰 저장 테스트")
//    public void refreshTokenSaveTest() {
//        //given
//        RefreshToken refreshToken = new RefreshToken("test@test.com", "refreshToken");
//
//        //when
//        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
//
//        //then
//        assertThat(savedRefreshToken).isSameAs(refreshToken);
//    }
//
//    @Test
//    @DisplayName("리프레시 토큰 id 기준 찾기 테스트")
//    public void refreshTokenFindByIdTest() {
//        //given
//        RefreshToken refreshToken = new RefreshToken("test@test.com", "refreshToken");
//        refreshTokenRepository.save(refreshToken);
//
//        //when
//        RefreshToken findRefreshToken = refreshTokenRepository.findById("test@test.com").orElse(null);
//
//        //then
//        assertThat(findRefreshToken).isNotNull();
//        assertThat(findRefreshToken.getUserEmail()).isEqualTo(refreshToken.getUserEmail());
//        assertThat(findRefreshToken.getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());
//    }
//
//    @Test
//    @DisplayName("리프레시 토큰 id 기준 삭제 테스트")
//    public void refreshTokenDeleteByIdTest() {
//        //given
//        RefreshToken refreshToken = new RefreshToken("test@test.com", "refreshToken");
//        refreshTokenRepository.save(refreshToken);
//
//        //when
//        refreshTokenRepository.deleteById("test@test.com");
//        RefreshToken findRefreshToken = refreshTokenRepository.findById("test@test.com").orElse(null);
//
//        //then
//        assertThat(findRefreshToken).isNull();
//    }
//}
