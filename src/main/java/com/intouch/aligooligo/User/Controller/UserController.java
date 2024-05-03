package com.intouch.aligooligo.User.Controller;

import com.intouch.aligooligo.User.Controller.Dto.LoginResponseDto;
import com.intouch.aligooligo.User.Service.UserService;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.ErrorMessage;
import com.intouch.aligooligo.exception.SocialLoginFailedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping(value="/users")
@RestController
public class UserController {
    private final UserService userService;

    @Operation(summary = "유저 소셜 로그인", description = "소셜 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDto.class)),
                    headers = @Header(name = "refresh Token", description = "리프레시 토큰, http-only설정, 헤더 속 쿠키로 반환")),
            @ApiResponse(responseCode = "401", description = "1. 카카오 엑세스 토큰을 가져오지 못했을 때 \t\n"
                    + "2. 카카오 유저 정보를 가져오지 못했을 때 \t\n 3. 레디스에 연결되지 못했을 때",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "서버 인증 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("/kakao")
    public ResponseEntity<?> SignInKakao(
            @RequestParam String code,
            HttpServletResponse response
    ) {
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
        } catch (RedisConnectionFailureException e) {
            return new ResponseEntity<>(new ErrorMessage("unable to connect redis"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
