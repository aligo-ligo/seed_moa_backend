package com.intouch.aligooligo.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping(value="/users")
@RestController
public class UserController {

    private final UserService userService;
    @PostMapping("/signin")
    public ResponseEntity<String> SignInUser(@RequestBody User req){
        try {
            String token = userService.SignIn(req);
            if(token==null)
                return ResponseEntity.internalServerError().build();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            return ResponseEntity.ok().headers(headers).body(token);
        }catch(IllegalArgumentException e){//db에 없을 때
            return ResponseEntity.status(401).build();
        }catch(UnsupportedJwtException e){//jwt가 예상하는 형식과 다른 형식이거나 구성
            return ResponseEntity.badRequest().build();
        }catch(MalformedJwtException e) {//잘못된 jwt 구조
            return ResponseEntity.badRequest().build();
        }catch(ExpiredJwtException e){//jwt 유효기간 초과
            return ResponseEntity.badRequest().build();
        }catch(SignatureException e){//jwt 서명실패(변조 데이터)
            return ResponseEntity.badRequest().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<String> SignUpUser(@RequestBody User req){
        try {
            int res = userService.SignUp(req);
            System.out.println(req);
            System.out.println(res);
            if (res == 0)
                return ResponseEntity.ok().body("회원가입이 완료되었습니다.");
            else if(res==1)
                return ResponseEntity.badRequest().body("올바른 이메일 형식이 아닙니다.");
            else if(res==2)
                return ResponseEntity.badRequest().body("영문과 숫자로 이루어진 비밀번호를 작성해주세요");
            else if(res==3)
                return ResponseEntity.badRequest().body("비밀번호가 8자 이하입니다.");
            else if(res==4)
                return ResponseEntity.badRequest().body("이메일, 비밀번호 혹은 닉네임 글자 수가 초과되었습니다.");
            else if(res==5)
                return ResponseEntity.badRequest().body("잘못된 요청입니다.");
            else if(res==6)
                return ResponseEntity.status(403).body("중복된 이메일입니다.");
            else
                return ResponseEntity.internalServerError().body("서버 에러");
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
