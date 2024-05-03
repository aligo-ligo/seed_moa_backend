package com.intouch.aligooligo.auth;

import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Service.UserService;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtTokenProvider jwtProvider;

    public TokenInfo reIssueToken(String refreshToken) {
        Claims claims = jwtProvider.parseClaims(refreshToken);
        User user = userService.findByUserEmail(claims.getSubject());

        RefreshToken findRefreshToken = refreshTokenService.findById(claims.getSubject());

        if (refreshToken.equals(findRefreshToken.getRefreshToken())) {
            return jwtProvider.createToken(user.getEmail(), user.getRoles());
        }

        refreshTokenService.deleteById(user.getEmail());
        log.error("리프레시 토큰이 일치하지 않아요.");
        throw new IllegalArgumentException("리프레시 토큰이 일치하지 않아요.");
    }
}
