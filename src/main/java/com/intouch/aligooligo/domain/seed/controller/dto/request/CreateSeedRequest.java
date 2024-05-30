package com.intouch.aligooligo.domain.seed.controller.dto.request;

import com.intouch.aligooligo.domain.seed.controller.dto.RoutineInfo;
import java.time.LocalDateTime;
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
    private LocalDateTime endDate;
    private String seed;
    private List<RoutineInfo> routines;
}
