package com.intouch.aligooligo.User;

import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    public String SignIn(User req) throws UnsupportedJwtException, MalformedJwtException, ExpiredJwtException, SignatureException{

        User user = findByUserEmail(req.getEmail()).orElseThrow(()->new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if(!passwordEncoder.matches(req.getPassword(),user.getPassword())){//email, password 모두 같을 경우
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(user.getEmail(),user.getRoles());
    }
    public Optional<User> findByUserEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
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
        System.out.println("hihihiihi");
        boolean checkedId = existByUserEmail(email);
        System.out.println("hihihiihi222");

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
                System.out.println("hihihiihisfasfdfs");
                System.out.println(passwordEncoder.encode(pw).length());
                //userRepository.save(new User(email,pw,name));
                userRepository.save(User.builder().email(email).password(passwordEncoder.encode(pw))
                        .nickName(name).roles(Collections.singletonList("ROLE_USER")).build());
                return 0;//ok
            }

            if(!email.contains("@"))
                return 1;//이메일 형식 올바르지 않은 경우
            if(!NumberCheck || !AlphabetCheck)
                return 2;//영문 or 숫자가 최소 하나 씩 들어가지 않은 경우
            if(pw.length()<8)
                return 3;//비밀번호 8자 이하인 경우
            if(email.length()>50||pw.length()>20||name.length()>10)
                return 4;//이메일 or 비밀번호 or 닉네임 글자 수 초과
            if(checkedId)
                return 6;//이메일 중복
            else{
                return 5;//bad request
            }
        }catch(Exception e){//server error
            e.printStackTrace();
            return 7;
        }
    }
}
