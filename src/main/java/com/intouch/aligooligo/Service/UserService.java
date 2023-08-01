package com.intouch.aligooligo.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.entity.User;
import com.intouch.aligooligo.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
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


    public String SignIn(User req, boolean kakaoChecked) throws UnsupportedJwtException, MalformedJwtException, ExpiredJwtException, SignatureException{

        User user = findByUserEmail(req.getEmail()).orElseThrow(()->new IllegalArgumentException("can't find userEmail"));
        if(kakaoChecked)
            return jwtTokenProvider.createToken(user.getEmail(),user.getRoles());
        else if(passwordEncoder.matches(req.getPassword(),user.getPassword())){//email, password 모두 같을 경우
            return jwtTokenProvider.createToken(user.getEmail(),user.getRoles());
        }
        return null;
    }
    public Optional<User> findByUserEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new IllegalArgumentException("don't exist user");
        return user;
    }
    public boolean existByUserEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public int SignUp(User req){
        try{
            return CheckValidation(req);
        }catch(Exception e){
            return 7;//server error
        }
    }
    public int CheckValidation(User req){
        boolean NumberCheck = false;
        boolean AlphabetCheck = false;
        String email = req.getEmail();
        String pw = req.getPassword();
        String name = req.getNickName();
        boolean checkedId = existByUserEmail(email);

        try{
            for(char i : pw.toCharArray()){
                if(Character.isDigit(i))
                    NumberCheck = true;
                if(Character.isAlphabetic(i))
                    AlphabetCheck = true;
            }
            if(email.contains("@") && NumberCheck && AlphabetCheck &&
            pw.length()>=8 && email.length()<=50 && pw.length()<=20 &&
            name.length()<=10 && !checkedId){
                userRepository.save(User.builder().email(email).password(passwordEncoder.encode(pw))
                        .nickName(name).roles(Collections.singletonList("ROLE_USER")).build());
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
        }catch(Exception e){//server error
            e.printStackTrace();
            return 6;
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
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + clientId);// TODO REST_API_KEY 입력
            sb.append("&redirect_uri=" + redirectUrl);// TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            sb.append("&client_secret=" + clientSecret);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonElement element = JsonParser.parseString(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        return access_Token;
    }

    public Map<String, String> createKakaoUser(String accessToken) {
        try{
            URL url = new URL(kakaoUserInfo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer "+accessToken);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리로 JSON파싱
            JsonElement element = JsonParser.parseString(result);
            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            String name = "";

            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
            name = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();
            br.close();

            if(!existByUserEmail(email)) {
                userRepository.save(User.builder().email(email)
                        .nickName(name).roles(Collections.singletonList("ROLE_USER")).build());
            }
            User user = findByUserEmail(email).get();
            List<String> list = user.getRoles();
            String localAccessToken = SignIn(new User(email,list), true);
            Map<String, String> returnValue = new HashMap<>();
            returnValue.put("id",user.getId().toString());
            returnValue.put("email",user.getEmail());
            returnValue.put("nickName",user.getNickName());
            returnValue.put("accessToken", localAccessToken);
            return returnValue;


        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
