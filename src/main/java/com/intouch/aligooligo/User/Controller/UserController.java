package com.intouch.aligooligo.User.Controller;

import com.intouch.aligooligo.User.Controller.Dto.LoginResponseDto;
import com.intouch.aligooligo.User.Service.UserService;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.ErrorMessage;
import com.intouch.aligooligo.exception.SocialLoginFailedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

@AllArgsConstructor
@RequestMapping(value="/users")
@RestController
@CrossOrigin
public class UserController {
    private final UserService userService;

    @PostMapping("/kakao")
    public ResponseEntity<?> SignInKakao(@RequestParam String code, HttpServletResponse response){
        try {
            TokenInfo tokenInfo = userService.kakaoLogin(code);
//            String access_Token = userService.getKakaoAccessToken(code);
//            TokenInfo tokenInfo = userService.getKakaoUserInfo(access_Token);

            Cookie cookie = new Cookie("refreshToken", tokenInfo.getRefreshToken());
            cookie.setMaxAge(14 * 24 * 60 * 60);//expires in 2 weeks

            cookie.setSecure(true);
            cookie.setHttpOnly(true);

            response.addCookie(cookie);
            String accessToken = tokenInfo.getAccessToken();
            Long accessTokenExpiredTime = tokenInfo.getAccessTokenValidTime();

            return new ResponseEntity<>(
                    new LoginResponseDto(accessToken, accessTokenExpiredTime), HttpStatus.OK);
        } catch (SocialLoginFailedException e) {
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()),HttpStatus.UNAUTHORIZED);
        }
    }
}
