package com.intouch.aligooligo.User.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.User.Controller.Dto.KakaoMember;
import com.intouch.aligooligo.User.Controller.Dto.KakaoToken;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.SocialLoginFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

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

    public User findByUserEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("don't exist user"));
    }
    public boolean existByUserEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public TokenInfo kakaoLogin(String code) {
        try {
            String kakaoAccessToken = getKakaoAccessToken(code);
            return getKakaoUserInfo(kakaoAccessToken);
        } catch (BadRequest e) {
            String msg = e.getMessage().split(": \"")[1];
            msg = msg.substring(0,msg.length()-1);
            JsonObject jsonObject = JsonParser.parseString(msg).getAsJsonObject();
            String errorDescription = jsonObject.get("error_description").getAsString();

            log.error(errorDescription);
            throw new SocialLoginFailedException(errorDescription);
        }
    }


    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUrl);
        body.add("code",code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        KakaoToken kakaoToken = restTemplate.postForObject("https://kauth.kakao.com/oauth/token",tokenRequest, KakaoToken.class);

        log.info("카카오 엑세스 토큰 받아옴");
        if (kakaoToken == null) {
            log.error("카카오 토큰 에러");
            return null;
        }
        return kakaoToken.getAccess_token();
    }

    public TokenInfo getKakaoUserInfo(String accessToken) {
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
                        .nickName(name).roles(Collections.singletonList("ROLE_USER")).build());
            }
            log.info("createKakaoUser success");

            return jwtTokenProvider.createToken(email, findByUserEmail(email).getRoles());

        }
        log.error("createKakaoUser error");
        throw new SocialLoginFailedException("failed getting kakao user info");
    }
}
