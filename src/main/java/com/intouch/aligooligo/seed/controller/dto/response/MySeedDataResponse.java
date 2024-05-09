package com.intouch.aligooligo.seed.controller.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySeedDataResponse {
    private String email;
    private String name;
    private List<StateStatistics> stateStatisticsList;

    @AllArgsConstructor
    @Getter
    public static class StateStatistics {
        private String state;
        private Long stateCount;
    }
}
