package com.intouch.aligooligo.seed.controller.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateSeedRequest {
    private String endDate;
    private String seed;
    private List<String> routines;
}
