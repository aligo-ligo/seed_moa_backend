package com.intouch.aligooligo.seed.controller.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SeedSharedResponse {
    private Long id;
    private String seedName;
    private String startDate;
    private String endDate;
    private Integer completedRoutineCount;
    private String seedState;
    private List<SharedRoutineDetail> routineDetails;
    private Long cheerUserCount;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SharedRoutineDetail {
        private Long routineId;
        private String routineTitle;
    }
}
