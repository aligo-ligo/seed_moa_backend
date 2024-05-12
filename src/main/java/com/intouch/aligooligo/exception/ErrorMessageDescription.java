package com.intouch.aligooligo.exception;

import lombok.Getter;

@Getter
public enum ErrorMessageDescription {
    UNKNOWN("알 수 없는 오류가 발생했습니다."),
    AUTHENTICATIONENTRYPOINT("로그인이 필요한 요청 입니다."),
    ACCESSEXPRIED("액세스 토큰이 만료되었습니다."),
    REISSUEFAILED("리프레시 토큰이 올바르지 않습니다. 다시 로그인해주세요.");

    private final String description;

    ErrorMessageDescription(String description) {
        this.description = description;
    }
}
