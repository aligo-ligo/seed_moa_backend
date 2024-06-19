package com.intouch.aligooligo.domain.seed.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SeedDetailResponse {
    private Long id;
    private String seedName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer completedRoutineCount;
    private String seedState;
    private List<RoutineDetail> routineDetails;
    private Long cheerUserCount;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoutineDetail {
        private Long routineId;
        private String routineTitle;
        private Boolean completedRoutineToday;
    }
}
