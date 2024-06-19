package com.intouch.aligooligo.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intouch.aligooligo.domain.member.entity.Member;
import com.intouch.aligooligo.domain.member.entity.Role;
import com.intouch.aligooligo.domain.member.repository.MemberRepository;
import com.intouch.aligooligo.domain.auth.dto.TokenInfo;
import com.intouch.aligooligo.domain.auth.entity.RefreshToken;
import com.intouch.aligooligo.domain.auth.service.AuthService;
import com.intouch.aligooligo.domain.auth.service.RefreshTokenService;
import com.intouch.aligooligo.domain.seed.domain.Seed;
import com.intouch.aligooligo.domain.seed.repository.SeedRepository;
import com.intouch.aligooligo.domain.seed.service.SeedService;
import com.intouch.aligooligo.global.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.global.exception.ErrorMessageDescription;
import io.jsonwebtoken.Jwts;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private SeedRepository seedRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Spy
    @InjectMocks
    private AuthService authService;
    @Mock
    private SeedService seedService;

    @Test
    @DisplayName("유저 이메일 찾기 테스트")
    void findUserByEmail() {
        //given
        String email = "test@test.com";
        String name = "test";
        Role role = Role.MEMBER;
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(new Member(email, name, role)));

        //when
        Member member = authService.findByUserEmail(email);

        assertThat(member.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("유저 이메일이 없을 때 테스트")
    void findUserByEmailException() {
        //given
        String email = "test@test.com";

        //when
        final IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class, () -> authService.findByUserEmail(email));

        //then
        assertThat(illegalArgumentException.getMessage())
                .isEqualTo("AuthService - findByUserEmail : 유저를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("유저 존재 확인 테스트")
    void existsByUserEmail() {
        //given
        String email = "test@test.com";
        String notExistEmail = "test2@test.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);
        when(memberRepository.existsByEmail(notExistEmail)).thenReturn(false);

        //when
        Boolean existUser = authService.existByUserEmail(email);
        Boolean notExistUser = authService.existByUserEmail(notExistEmail);

        //then
        assertThat(existUser).isTrue();
        assertThat(notExistUser).isFalse();
    }

    @Test
    @DisplayName("유저 탈퇴 로직 확인")
    void withdrawalUserTest() {
        //given
        String email = "test2@test.com";
        String name = "test";
        Role role = Role.MEMBER;
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(new Member(email, name, role)));
        when(seedRepository.findByMemberId(any())).thenReturn(List.of(new Seed()));

        //when
        authService.withdrawalUser(email);

        //then
        verify(authService).findByUserEmail(email);
        verify(seedRepository).findByMemberId(any());
        verify(seedService).deleteSeed(any());
        verify(memberRepository).deleteById(any());
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void tokenReissueTest() {
        //given
        String userPk = "test@test.com";
        String name = "test";
        String accessToken = "access1234";
        String refreshToken = "refresh1234";
        when(jwtTokenProvider.parseClaims(refreshToken)).thenReturn(Jwts.claims().setSubject(userPk));
        when(memberRepository.findByEmail(userPk)).thenReturn(Optional.of(new Member(userPk, name, Role.MEMBER)));
        when(refreshTokenService.findById(userPk)).thenReturn(new RefreshToken(userPk, refreshToken));
        when(jwtTokenProvider.createToken(userPk, Role.MEMBER)).thenReturn(new TokenInfo(accessToken, refreshToken));

        //when
        TokenInfo tokenInfo = authService.reIssueToken(refreshToken);

        //then
        assertThat(tokenInfo.getAccessToken()).isEqualTo(accessToken);
        assertThat(tokenInfo.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("잘못된 토큰 입력 시 재발급 테스트")
    void WrongRefreshTokenReissueTest() {
        //given
        String userPk = "test@test.com";
        String name = "test";
        String refreshToken = "refresh1234";
        String savedRefreshToken = "savedRefresh1234";
        when(jwtTokenProvider.parseClaims(refreshToken)).thenReturn(Jwts.claims().setSubject(userPk));
        when(memberRepository.findByEmail(userPk)).thenReturn(Optional.of(new Member(userPk, name, Role.MEMBER)));
        when(refreshTokenService.findById(userPk)).thenReturn(new RefreshToken(userPk, savedRefreshToken));

        //when
        final IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class, () -> authService.reIssueToken(refreshToken));

        //then
        assertThat(illegalArgumentException.getMessage())
                .isEqualTo(ErrorMessageDescription.REISSUE_FAILED.getDescription());
        verify(refreshTokenService, times(1)).deleteById(userPk);
    }

    //카카오 테스트 추후 작성
}
