package com.intouch.aligooligo.seed.controller.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SeedSharedResponse {
    private String seed;
    private String startDate;
    private String endDate;
    private Integer completedRoutineCount;
    private String seedState;
    private List<SharedRoutineDetail> routineDetails;
    private List<CheeringUser> cheeringUserList;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SharedRoutineDetail {
        private Long routineId;
        private String routineTitle;
    }
}
