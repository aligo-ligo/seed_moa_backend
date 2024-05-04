package com.intouch.aligooligo.seed.controller.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateSeedRequest {
    private String oldRoutineTitle;
    private String newRoutineTitle;
}
