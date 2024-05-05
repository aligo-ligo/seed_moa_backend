package com.intouch.aligooligo.seed.controller.dto.request;

import com.intouch.aligooligo.seed.controller.dto.RoutineInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSeedRequest {
    private String endDate;
    private String seed;
    private List<RoutineInfo> routines;
}
