package com.intouch.aligooligo.User;

import com.intouch.aligooligo.Exception.UserIdPwNotFoundException;
import com.intouch.aligooligo.Jwt.JwtProvider;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    public String SignIn(UserDTO req) throws UnsupportedJwtException, MalformedJwtException, ExpiredJwtException, SignatureException{
        try {
            UserDTO user = findByUserEmail(req.getEmail()).get();
            if(Objects.equals(req.getPassword(), user.getPassword())){//email, password 모두 같을 경우
                System.out.println("hi");
                return jwtProvider.createToken(user.getEmail());
            }
            else{
                throw new UserIdPwNotFoundException("비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Optional<UserDTO> findByUserEmail(String email) throws UserIdPwNotFoundException{
        Optional<UserDTO> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new UserIdPwNotFoundException("사용자를 찾을 수 없습니다.");
        return user;
    }
    public int SignUp(UserDTO req){
        try{
            return CheckValidation(req);
        }catch(Exception e){
            return 7;//server error
        }
    }
    public int CheckValidation(UserDTO req){
        boolean NumberCheck = false;
        boolean AlphabetCheck = false;
        String email = req.getEmail();
        String pw = req.getPassword();
        String name = req.getNickName();
        Optional<UserDTO> user = userRepository.findByEmail(email);
        try{
            for(char i : pw.toCharArray()){
                if(Character.isDigit(i))
                    NumberCheck = true;
                if(Character.isAlphabetic(i))
                    AlphabetCheck = true;
            }
            if(email.contains("@") && NumberCheck && AlphabetCheck &&
            pw.length()>=8 && email.length()<=50 && pw.length()<=20 &&
            name.length()<=10 && user.isEmpty()){
                userRepository.save(new UserDTO(email,pw,name));
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
            if(user.isPresent())
                return 6;//이메일 중복
            else{
                return 5;//bad request
            }
        }catch(Exception e){//server error
            return 7;
        }
    }
}
