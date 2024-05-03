package com.intouch.aligooligo.User.Controller.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class LoginResponseDto {
    private String accessToken;
    private Long accessTokenExpiredTime;
}
