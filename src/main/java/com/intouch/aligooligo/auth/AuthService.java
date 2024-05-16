package com.intouch.aligooligo.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.User.Entity.Role;
import com.intouch.aligooligo.auth.dto.KakaoMember;
import com.intouch.aligooligo.auth.dto.KakaoToken;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.ErrorMessageDescription;
import com.intouch.aligooligo.exception.SocialLoginFailedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;

    @Value("${kakaoUrl}")
    private String kakaoUrl;

    @Value("${redirectUrl}")
    private String redirectUrl;

    @Value("${clientId}")
    private String clientId;

    @Value("${clientSecret}")
    private String clientSecret;

    @Value("${kakaoUserInfo}")
    private String kakaoUserInfo;

    public TokenInfo reIssueToken(String refreshToken) {
        try {
            Claims claims = jwtProvider.parseClaims(refreshToken);
            User user = findByUserEmail(claims.getSubject());

            RefreshToken findRefreshToken = refreshTokenService.findById(claims.getSubject());

            if (refreshToken.equals(findRefreshToken.getRefreshToken())) {
                return jwtProvider.createToken(user.getEmail(), user.getRoles());
            }

            refreshTokenService.deleteById(user.getEmail());
            log.error("AuthService - reIssueToken : 리프레시 토큰이 일치하지 않아요.");
            throw new IllegalArgumentException(ErrorMessageDescription.REISSUE_FAILED.getDescription());
        } catch (IllegalArgumentException e) {
            log.info(e.getMessage());
            throw new IllegalArgumentException(ErrorMessageDescription.REISSUE_FAILED.getDescription());
        }
    }

    public User findByUserEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->
            new IllegalArgumentException("AuthService - findByUserEmail : 유저를 찾을 수 없습니다."));
    }
    public boolean existByUserEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public TokenInfo kakaoLogin(HttpServletRequest request, String code) {
        try {
            log.info(request.getRemoteHost());
            String kakaoAccessToken = getKakaoAccessToken("http://" + request.getRemoteHost() + "/kakao", code);
            return getKakaoUserInfo(kakaoAccessToken);
        } catch (BadRequest e) {
            String msg = e.getMessage().split(": \"")[1];
            msg = msg.substring(0,msg.length()-1);
            JsonObject jsonObject = JsonParser.parseString(msg).getAsJsonObject();
            String errorDescription = jsonObject.get("error_description").getAsString();

            log.error(String.format("AuthService - kakaoLogin : %s",errorDescription));
            throw new SocialLoginFailedException(ErrorMessageDescription.UNKNOWN.getDescription());
        }
    }


    public String getKakaoAccessToken(String dynamicRedirectUri, String code) {
        log.info("getKakaoAccessToken");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", dynamicRedirectUri);
        body.add("code",code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        KakaoToken kakaoToken = restTemplate.postForObject("https://kauth.kakao.com/oauth/token",tokenRequest, KakaoToken.class);

        if (kakaoToken == null) {
            return null;
        }
        return kakaoToken.getAccess_token();
    }

    public TokenInfo getKakaoUserInfo(String accessToken) {
        log.info("getKakaoUserInfo");
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + accessToken);

        //헤더 + 바디
        HttpEntity<MultiValueMap<String, String>> memberInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<KakaoMember> kakaoMember = restTemplate.exchange(kakaoUserInfo, HttpMethod.GET, memberInfoRequest, KakaoMember.class);

        if (kakaoMember.getBody() != null) {
            String email = kakaoMember.getBody().getKakaoAccount().getEmail();
            String name = kakaoMember.getBody().getKakaoProperties().getNickName();

            if(!existByUserEmail(email)) {
                userRepository.save(User.builder().email(email)
                        .nickName(name).roles(Role.USER).build());
            }

            return jwtProvider.createToken(email, findByUserEmail(email).getRoles());

        }
        throw new SocialLoginFailedException(ErrorMessageDescription.UNKNOWN.getDescription());
    }
}
