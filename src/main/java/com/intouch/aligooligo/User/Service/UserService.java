package com.intouch.aligooligo.User.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.User.Controller.Dto.UserLoginResponseDto;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
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
            response.setAccessToken(jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles()));//exception
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

    public String getKakaoAcessToken(String code){
        String access_Token = "";
        String refresh_Token = "";

        try{
            URL url = new URL(kakaoUrl);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=authorization_code" +
                    "&client_id=" + clientId +// TODO REST_API_KEY 입력
                    "&redirect_uri=" + redirectUrl +// TODO 인가코드 받은 redirect_uri 입력
                    "&code=" + code +
                    "&client_secret=" + clientSecret;
            bw.write(sb);
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonElement element = JsonParser.parseString(result.toString());

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();

            return access_Token;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public UserLoginResponseDto createKakaoUser(String accessToken) {
        try{
            URL url = new URL(kakaoUserInfo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer "+accessToken);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            //Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(result.toString());
            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = null;
            String name;

            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
            name = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();
            br.close();

            if(!existByUserEmail(email)) {
                userRepository.save(User.builder().email(email)
                        .nickName(name).roles(Collections.singletonList("ROLE_USER")).build());
            }

            UserLoginResponseDto response = SignIn(new User(email, findByUserEmail(email).getRoles()), true);
            return response;

        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
