package com.intouch.aligooligo.seed.controller.dto.response;

import com.intouch.aligooligo.seed.domain.Cheering;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
public class SeedDetailResponse {
    private Long id;
    private String startDate;
    private String endDate;
    private Integer completedRoutineCount;
    private String seedState;
    private List<RoutineDetail> routineDetails;
    private List<CheeringUserName> cheeringList;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoutineDetail {
        private Long routineId;
        private String routineTitle;
        private Boolean completedRoutineToday;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheeringUserName {
        private String userName;
    }


}
