package com.intouch.aligooligo.init.controller;

import com.intouch.aligooligo.init.controller.dto.InitDataResponse;
import com.intouch.aligooligo.init.service.InitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/init")
@AllArgsConstructor
@Tag(name = "init", description = "처음 화면 관련 API")
public class InitController {
    private final InitService initService;

    @Operation(summary = "총 유저 수와 총 씨앗 수 조회", description = "총 유저의 수와 총 씨앗의 수를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InitDataResponse.class)))
    })
    @GetMapping
    public ResponseEntity<?> getInitData() {
        InitDataResponse initDataResponse = initService.getInitData();
        return new ResponseEntity<>(initDataResponse, HttpStatus.OK);
    }
}
