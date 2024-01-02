package com.intouch.aligooligo.User.Controller;

import com.intouch.aligooligo.User.Controller.Dto.UserLoginResponseDto;
import com.intouch.aligooligo.User.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping(value="/users")
@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;

    //카카오 유저 로그인
    @GetMapping("/kakao")
    public ResponseEntity<UserLoginResponseDto> SignUpKakao(@RequestParam String code){
        String access_Token = userService.getKakaoAcessToken(code);
        UserLoginResponseDto response = userService.createKakaoUser(access_Token);
        if(response!=null)
            return ResponseEntity.ok().body(response);
        return ResponseEntity.internalServerError().build();
    }
}
