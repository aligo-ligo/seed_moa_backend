package com.intouch.aligooligo.Jwt;

import com.intouch.aligooligo.User.Entity.Role;
import com.intouch.aligooligo.auth.RefreshTokenService;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${secretKey}")
    private String secretKey;

    private final RefreshTokenService refreshTokenService;
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String REFRESH_HEADER = "RefreshToken";
    private final static String BEARER_TYPE = "Bearer";


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    // JWT 토큰 생성
    public TokenInfo createToken(String userPk, Role roles) {
        log.info("createToken 시작");
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles.name()); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();
        long accessTokenValidTime = now.getTime() + 180 * 60 * 1000L;
        long refreshTokenValidTime = now.getTime() + 14 * 24 * 60 * 60 * 1000L;

        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(accessTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        refreshTokenService.saveTokenInfo(userPk, refreshToken);

        return new TokenInfo(accessToken, refreshToken, accessTokenValidTime);
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);

        if (claims.get("roles") == null) {
            //TODO:: Change Custom Exception
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        //클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("roles").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰에서 회원 정보 추출
    public Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(REFRESH_HEADER);
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            if (jwtToken == null) {
                log.info("jwtToken is null");
            }
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.error("JwtTokenProvider - validateToken() : 액세스 토큰이 만료되었습니다.");
            throw new JwtException("액세스 토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            log.error(String.format("JwtTokenProvider - validateToken() : %s", e.getMessage()));
            throw new JwtException("알 수 없는 오류가 발생했습니다.");
        }
    }
}
