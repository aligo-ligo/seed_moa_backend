package com.intouch.aligooligo.auth;

import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.ErrorMessage;
import com.intouch.aligooligo.exception.ErrorMessageDescription;
import com.intouch.aligooligo.exception.SocialLoginFailedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenInfo.class))),
            @ApiResponse(responseCode = "401", description = "1. 헤더에 refresh token이 없을 때\t\n 2. refresh token이 일치하지 않을 때",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> reissueToken(HttpServletRequest request) {

        String encryptedRefreshToken = jwtProvider.resolveRefreshToken(request);
        if (encryptedRefreshToken == null) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.REISSUE_FAILED.getDescription()),
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            TokenInfo tokenInfo = authService.reIssueToken(encryptedRefreshToken);

            String accessToken = tokenInfo.getAccessToken();
            String refreshToken = tokenInfo.getRefreshToken();
            Long accessTokenValidTime = tokenInfo.getAccessTokenValidTime();

            return new ResponseEntity<>(new TokenInfo(accessToken, refreshToken, accessTokenValidTime), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.REISSUE_FAILED.getDescription()), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "유저 소셜 로그인", description = "소셜 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenInfo.class))),
            @ApiResponse(responseCode = "401", description = "1. 카카오 엑세스 토큰을 가져오지 못했을 때 \t\n"
                    + "2. 카카오 유저 정보를 가져오지 못했을 때 \t\n 3. 레디스에 연결되지 못했을 때",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "서버 인증 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("/kakao")
    public ResponseEntity<?> SignInKakao(@RequestParam String code) {
        try {
            TokenInfo tokenInfo = authService.kakaoLogin(code);

            String accessToken = tokenInfo.getAccessToken();
            String refreshToken = tokenInfo.getRefreshToken();
            Long accessTokenExpiredTime = tokenInfo.getAccessTokenValidTime();

            return new ResponseEntity<>(
                    new TokenInfo(accessToken, refreshToken, accessTokenExpiredTime), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.UNKNOWN.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
