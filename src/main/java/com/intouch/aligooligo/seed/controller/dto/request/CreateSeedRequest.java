package com.intouch.aligooligo.seed.controller.dto.request;

import com.intouch.aligooligo.seed.controller.dto.RoutineInfo;
import io.swagger.v3.oas.annotations.media.Schema;
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
