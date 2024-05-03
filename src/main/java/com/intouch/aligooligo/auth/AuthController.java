package com.intouch.aligooligo.auth;

import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.User.Controller.Dto.LoginResponseDto;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtProvider;
    private final AuthService authService;

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class)),
                    headers = @Header(name = "refresh Token", description = "리프레시 토큰, http-only설정, 헤더 속 쿠키로 반환")),
            @ApiResponse(responseCode = "401", description = "1. 헤더에 refresh token이 없을 때\t\n 2. refresh token이 일치하지 않을 때",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    @Parameter(name = "refresh token", in = ParameterIn.HEADER)
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
