package com.intouch.aligooligo.seed.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CheerMediateResponse {

    @Schema(example = "\"add\" || \"delete\"")
    private String message;
}
