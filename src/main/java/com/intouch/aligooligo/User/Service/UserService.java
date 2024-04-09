package com.intouch.aligooligo.User.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.User.Controller.Dto.KakaoMember;
import com.intouch.aligooligo.User.Controller.Dto.KakaoToken;
import com.intouch.aligooligo.User.Controller.Dto.UserLoginResponseDto;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

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

    public UserLoginResponseDto SignIn(User req, boolean kakaoChecked) {
        User user;
        UserLoginResponseDto response = new UserLoginResponseDto();

        user = findByUserEmail(req.getEmail());
        if(kakaoChecked || passwordEncoder.matches(req.getPassword(),user.getPassword())){
            response.setAccessToken(jwtTokenProvider.createToken(user.getEmail(), user.getRoles()));//exception
            response.setUserLoginDTO(new UserLoginResponseDto.UserLoginDTO(user.getNickName()));
            return response;
        }
        return null;
    }
    public User findByUserEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("don't exist user"));
    }
    public boolean existByUserEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void saveUser(String email, String pw, String nickname, List<String> roles) {
        try {
            userRepository.save(User.builder().email(email).password(passwordEncoder.encode(pw))
                    .nickName(nickname).roles(roles).build());
        }catch (ConstraintViolationException e){
            throw e;
        }
    }

    public Integer SignUp(User req){
        return CheckValidation(req);
    }
    public Integer CheckValidation(User req){
        boolean NumberCheck = false;
        boolean AlphabetCheck = false;
        String email = req.getEmail();
        String pw = req.getPassword();
        String name = req.getNickName();
        boolean checkedId = existByUserEmail(email);

        for(char i : pw.toCharArray()){
            if(Character.isDigit(i))
                NumberCheck = true;
            if(Character.isAlphabetic(i))
                AlphabetCheck = true;
        }
        if(email.contains("@") && NumberCheck && AlphabetCheck &&
                pw.length()>=8 && email.length()<=50 && pw.length()<=20 &&
                name.length()<=10 && !checkedId){

            saveUser(email,pw,name,Collections.singletonList("ROLE_USER"));

            return 0;//ok
        }
        if(!email.contains("@")||email.length()>50)
            return 1;//이메일 형식 올바르지 않은 경우
        if(checkedId)
            return 2;//이메일 중복
        if(!NumberCheck || !AlphabetCheck || pw.length()>20 || pw.length()<8)
            return 3;//영문 or 숫자가 최소 하나 씩 들어가지 않은 경우, 길이 문제
        if(name.length()>10)
            return 4;//닉네임 길이문제
        else{
            return 5;//bad request
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

        if (kakaoToken == null) {
            log.error("카카오 토큰 에러");
            return null;
        }
        return kakaoToken.getAccess_token();
    }

    public UserLoginResponseDto getKakaoUserInfo(String accessToken) {
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
            log.info("createKakaoUser before signin");
            UserLoginResponseDto response = SignIn(new User(email, findByUserEmail(email).getRoles()), true);
            log.info("createKakaoUser end");
            return response;
        }
        log.error("createKakaoUser error");
        return null;
    }
}
