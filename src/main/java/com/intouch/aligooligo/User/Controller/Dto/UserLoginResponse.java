package com.intouch.aligooligo.User.Controller.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserLoginResponse {


    @AllArgsConstructor
    @Setter
    @Getter
    public static class UserLoginDTO{
        private String nickName;
    }
    private String accessToken;
    private UserLoginDTO userLoginDTO;
}
