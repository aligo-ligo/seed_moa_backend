package com.intouch.aligooligo.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping(value="/users")
@RestController
@CrossOrigin
public class UserController {

    private final UserService userService;
    public String getToken(User req){
        try {
            String token = userService.SignIn(req);
            if(token==null)
                return "400";
            return token;
        }catch(IllegalArgumentException e){//db에 없을 때
            return "401";
        }catch(UnsupportedJwtException e){//jwt가 예상하는 형식과 다른 형식이거나 구성
            return "400";
        }catch(MalformedJwtException e) {//잘못된 jwt 구조
            return "400";
        }catch(ExpiredJwtException e){//jwt 유효기간 초과
            return "400";
        }catch(SignatureException e){//jwt 서명실패(변조 데이터)
            return "400";
        } catch (Exception e){
            return "500";
        }
    }

    public String getResponse(String token, User user){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode mainNode = objectMapper.createObjectNode();
        ObjectNode userNode = objectMapper.createObjectNode();

        userNode.put("email", user.getEmail());
        userNode.put("nickName",user.getNickName());
        mainNode.put("accessToken",token);
        mainNode.set("user",userNode);

        return mainNode.toString();
    }

    @PostMapping("/signin")
    public ResponseEntity<String> SignInUser(@RequestBody User req){
        String token = getToken(req);

        if(token==null)
            return ResponseEntity.internalServerError().build();
        else if(token.equals("401"))
            return ResponseEntity.status(401).build();
        else if(token.equals("400"))
            return ResponseEntity.badRequest().build();
        else if(token.equals("500"))
            return ResponseEntity.internalServerError().build();
        return ResponseEntity.ok().body(getResponse(token, req));

    }
    @PostMapping("/signup")
    public ResponseEntity<String> SignUpUser(@RequestBody User req){
        try {
            int res = userService.SignUp(req);
            System.out.println(req);
            System.out.println(res);
            if (res == 0){
                String token = getToken(req);
                System.out.println(token);
                if(token==null)
                    return ResponseEntity.internalServerError().build();
                else if(token.equals("401"))
                    return ResponseEntity.status(401).build();
                else if(token.equals("400"))
                    return ResponseEntity.badRequest().build();
                else if(token.equals("500"))
                    return ResponseEntity.internalServerError().build();
                return ResponseEntity.ok().body(getResponse(token, req));
            }
            else if(res==1)
                return ResponseEntity.badRequest().body("Email format is invalid");
            else if(res==2)
                return ResponseEntity.badRequest().body("Password is invalid");
            else if(res==3)
                return ResponseEntity.badRequest().body("nickName is invalid");
            else if(res==4)
                return ResponseEntity.badRequest().body("Email already exists");
            else if(res==5)
                return ResponseEntity.badRequest().body("Bad request");
            else if(res==6)
                return ResponseEntity.status(403).body("Email already exists");
            else
                return ResponseEntity.internalServerError().body("Server error");
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/kakao")
    public ResponseEntity<HttpStatus> SignUpKakao(@RequestParam String code){
        String access_Token = userService.getKakaoAcessToken(code);
        System.out.println("controller access_token: "+access_Token);
        boolean createKakaoUser = userService.createKakaoUser(access_Token);

        if(createKakaoUser)
            return ResponseEntity.ok().build();
        return ResponseEntity.internalServerError().build();

    }


}
