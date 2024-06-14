package com.intouch.aligooligo.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intouch.aligooligo.domain.auth.controller.AuthController;
import com.intouch.aligooligo.domain.auth.dto.SignInResponse;
import com.intouch.aligooligo.domain.auth.dto.TokenInfo;
import com.intouch.aligooligo.domain.auth.service.AuthService;
import com.intouch.aligooligo.global.Jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @MockBean
    private AuthService authService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    private TokenInfo tokenInfo;

    @BeforeEach
    public void initMockMvc() {
        tokenInfo = new TokenInfo("accessToken", "refreshToken");
    }
    @Test
    @WithMockUser
    @DisplayName("토큰 재발급 컨트롤러 테스트")
    void reissueTokenTest() throws Exception {
        String refreshToken = "refreshToken";
        given(jwtTokenProvider.resolveRefreshToken(any(HttpServletRequest.class))).willReturn(refreshToken);
        given(authService.reIssueToken(any(String.class))).willReturn(tokenInfo);

        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/reissue")
                .header("RefreshToken", "refreshToken")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("유저 로그인 컨트롤러 테스트")
    void signInKakao() throws Exception {
        //given
        String code = "authorizeCode";
        tokenInfo = new TokenInfo("accessToken", "refreshToken");
        given(authService.kakaoLogin(code)).willReturn(new SignInResponse(tokenInfo, true));

        //when
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/kakao")
                .param("code", code)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 회원 탈퇴 테스트")
    @WithMockUser
    void withdrawalUserTest() throws Exception{
        //given
        String accessToken = "accessToken";
        String userPk = "test222@test.com";
        Claims claims = Jwts.claims().setSubject(userPk);
        given(jwtTokenProvider.resolveAccessToken(any(HttpServletRequest.class))).willReturn(accessToken);
        given(jwtTokenProvider.parseClaims(accessToken)).willReturn(claims);

        //when
        MockHttpServletRequestBuilder requestBuilder = delete("/api/auth")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //then
        resultActions.andExpect(status().isOk());
    }


}
