package com.intouch.aligooligo.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoMember {

    @JsonProperty("id")
    private String socialId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("properties")
    private KakaoProperties kakaoProperties;

    @Getter
    public static class KakaoProperties {
        @JsonProperty("nickname")
        private String nickName;
    }

    @Getter
    public static class KakaoAccount {
        @JsonProperty("email")
        private String email;

    }
}
