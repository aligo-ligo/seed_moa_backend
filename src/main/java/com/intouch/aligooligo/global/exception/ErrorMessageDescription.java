package com.intouch.aligooligo.global.exception;

import lombok.Getter;

@Getter
public enum ErrorMessageDescription {
    UNKNOWN("알 수 없는 오류가 발생했습니다."),
    AUTHENTICATION_ENTRY_POINT("로그인이 필요한 요청 입니다."),
    AUTHORIZATION_DENIED("해당 권한이 없습니다."),
    ACCESS_EXPRIED("액세스 토큰이 만료되었습니다."),
    REISSUE_FAILED("리프레시 토큰이 올바르지 않습니다. 다시 로그인해주세요."),
    SEED_NOT_FOUND("존재하지 않는 씨앗입니다."),
    UNSUPPORTSEED_TYPE("씨앗 생성 폼 형식을 올바르게 작성해주세요."),
    ROUTINE_NO_EXISTED("루틴 내용이 존재하지 않습니다."),
    ALREADY_EXIST_LIKE("이미 응원중인 씨앗입니다.");

    private final String description;

    ErrorMessageDescription(String description) {
        this.description = description;
    }
}
