package com.intouch.aligooligo.auth;

import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.User.Controller.Dto.LoginResponseDto;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.ErrorMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtProvider;
    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String encryptedRefreshToken = jwtProvider.resolveRefreshToken(request);
        if (encryptedRefreshToken == null) {
            return new ResponseEntity<>(new ErrorMessage("헤더에 refresh token이 없습니다. 다시 로그인해주세요."),
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            TokenInfo tokenInfo = authService.reIssueToken(encryptedRefreshToken);

            Cookie cookie = new Cookie("refreshToken", tokenInfo.getRefreshToken());
            cookie.setMaxAge(14*24*60*60);//expires in 2 weeks

            cookie.setSecure(true);
            cookie.setHttpOnly(true);

            response.addCookie(cookie);
            String accessToken = tokenInfo.getAccessToken();

            return new ResponseEntity<>(new LoginResponseDto(accessToken, tokenInfo.getAccessTokenValidTime()), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()),HttpStatus.UNAUTHORIZED);
        }
    }

}
