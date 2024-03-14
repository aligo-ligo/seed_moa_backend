package com.intouch.aligooligo.User.Controller.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDto {
    private String email;
    private String password;
    private String nickName;
}
