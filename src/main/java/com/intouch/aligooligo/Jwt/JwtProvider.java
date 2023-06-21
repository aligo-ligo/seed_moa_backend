package com.intouch.aligooligo.Jwt;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {
    private final String secretKey = "aligooligoproject";

    public String createToken(String email){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofDays(3).toMillis()); // 만료기간 3일
        Claims claims = Jwts.claims();
        claims.put("userEmail", email);



        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .setClaims(claims)
                .setIssuer("aligo-oligo") // 토큰발급자(iss)
                .setIssuedAt(now) // 발급시간(iat)
                .setExpiration(expiration) // 만료시간(exp)
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(secretKey.getBytes())) // 알고리즘, 시크릿 키
                .compact();
    }

    //==Jwt 토큰의 유효성 체크 메소드==//
    public Claims parseJwtToken(String token) {
        token = BearerRemove(token); // Bearer 제거
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }

    //==토큰 앞 부분('Bearer') 제거 메소드==//
    private String BearerRemove(String token) {
        return token.substring("Bearer ".length());
    }
}
