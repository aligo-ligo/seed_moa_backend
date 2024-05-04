package com.intouch.aligooligo.seed.controller.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeedDetailResponse {
    private String seed;
    private String startDate;
    private String endDate;
    private Integer completedRoutineCount;
    private Map<String, Boolean> routines;
    private String state;
}
