package com.intouch.aligooligo.User.Controller;

import com.intouch.aligooligo.User.Controller.Dto.UserLoginResponseDto;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping(value="/users")
@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<Object> SignInUser(@RequestBody User req) {
        UserLoginResponseDto userLoginResponseDto;
        try{
            userLoginResponseDto = userService.SignIn(req, false);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Cannot find user");
        }catch (UnsupportedJwtException e){
            return ResponseEntity.status(401).body("not support jwt algorithm");
        } catch(MalformedJwtException e){
            return ResponseEntity.status(401).body("malformed jwt");
        } catch(ExpiredJwtException e){
            return ResponseEntity.status(401).body("token expired");
        } catch(SignatureException e){
            return ResponseEntity.status(401).body("not valid jwt signature");
        }
        if(userLoginResponseDto ==null)
            return ResponseEntity.badRequest().body("Incorrect password");
        else
            return ResponseEntity.ok().body(userLoginResponseDto);
    }
    @PostMapping("/signup")
    public ResponseEntity<Object> SignUpUser(@RequestBody User req)throws Exception{

        int res = userService.SignUp(req);
        switch (res) {
            case 0 -> {
                UserLoginResponseDto userLoginResponseDto = userService.SignIn(req,false);
                return ResponseEntity.ok().body(userLoginResponseDto);
            }
            case 1 -> {return ResponseEntity.badRequest().body("Email format is invalid");}
            case 2 -> {return ResponseEntity.badRequest().body("Email already exists");}
            case 3 -> {return ResponseEntity.badRequest().body("Password is invalid");}
            case 4 -> {return ResponseEntity.badRequest().body("nickName is invalid");}
            case 5 -> {return ResponseEntity.badRequest().body("Bad request");}
            default -> {return ResponseEntity.internalServerError().body("unknown server error");}
        }
    }
    @PostMapping("/kakao")
    public ResponseEntity<UserLoginResponseDto> SignUpKakao(@RequestParam String code){
        String access_Token = userService.getKakaoAcessToken(code);
        UserLoginResponseDto response = userService.createKakaoUser(access_Token);
        if(response!=null)
            return ResponseEntity.ok().body(response);
        return ResponseEntity.internalServerError().build();
    }
}
