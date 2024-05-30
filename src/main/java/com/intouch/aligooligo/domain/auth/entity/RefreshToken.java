package com.intouch.aligooligo.domain.auth.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 14)//2주
public class RefreshToken {
    @Id
    private String userEmail;


    private String refreshToken;

//60*60*24*3

}
