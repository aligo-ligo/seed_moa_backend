package com.intouch.aligooligo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponse {
    private TokenInfo tokenInfo;
    private Boolean isFirst;
}
