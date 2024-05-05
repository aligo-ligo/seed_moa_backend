package com.intouch.aligooligo.seed.controller.dto.response;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class SeedDetailResponse {
    private String seed;
    private String startDate;
    private String endDate;
    private Integer completedRoutineCount;
    private String state;
    private List<RoutineDetail> routineDetails;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoutineDetail {
        private Long routineId;
        private String routineTitle;
        private Boolean completedRoutineToday;
    }


}
