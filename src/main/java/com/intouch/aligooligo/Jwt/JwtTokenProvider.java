package com.intouch.aligooligo.Jwt;

import com.intouch.aligooligo.security.CustomUserDetailService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${secretKey}")
    private String secretKey;

    private final CustomUserDetailService userDetailsService;

    private final String ACCESS_TOKEN_SUBJECT = "ACCESS_TOKEN";
    private final String REFRESH_TOKEN_SUBJECT = "REFRESH_TOKEN";

    private final long ACCESS_TOKEN_VALID_TIME = 60 * 60 * 1000L;//60분
    private final long REFRESH_TOKEN_VALID_TIME = 14 * 24 * 60 * 60 * 1000L;//2주

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    // JWT 토큰 생성
    public String createAccessToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(ACCESS_TOKEN_SUBJECT); // JWT payload 에 저장되는 정보단위
        claims.put("userId", userPk);
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    public String createRefreshToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(REFRESH_TOKEN_SUBJECT);
        claims.put("userId", userPk);
        claims.put("roles", roles);
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        //redisService.setValues(String.valueOf(userPk), refreshToken, REFRESH_TOKEN_VALID_TIME);
        return refreshToken;
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Claims claims = getClaims(jwtToken);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
            //throw new CustomException(Result.BAD_REQUEST);
        } //catch (ExpiredJwtException e) {
            //throw new CustomException(Result.BAD_REQUEST);
        //}
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch(JwtException e) {
            return null;
            //throw new CustomException(Result.BAD_REQUEST);
        }
    }
}
