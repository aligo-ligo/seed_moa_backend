package com.intouch.aligooligo.domain.seed.controller.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CheerInfo {

    private List<CheerUser> cheerUsers;
    private Long cheerCount;

    @Getter
    @AllArgsConstructor
    public static class CheerUser {
        private Long id;
        private CheererInfo cheererInfo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheererInfo {
        private String cheererName;
    }
}
