package com.intouch.aligooligo.seed.controller;


import com.intouch.aligooligo.exception.DataNotFoundException;
import com.intouch.aligooligo.exception.ErrorMessage;
import com.intouch.aligooligo.exception.ErrorMessageDescription;
import com.intouch.aligooligo.seed.controller.dto.request.UpdateSeedRequest;
import com.intouch.aligooligo.seed.service.RoutineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping(value="/api/seed/routine")
@RestController
@Tag(name = "routine", description = "루틴 관련 API")
public class RoutineController {
    private final RoutineService routineService;


    @PatchMapping("/{id}")
    @Operation(summary = "루틴 수정", description = "루틴 수정 API, 하나만 수정 가능, 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "500", description = "기타 서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> updateRoutine(@PathVariable("id") Long routineId,
            @RequestBody UpdateSeedRequest updateSeedRequest) {
        try{
            routineService.updateSeed(routineId, updateSeedRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataNotFoundException e) {
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.UNKNOWN.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/end/{id}")
    @Operation(summary = "오늘 루틴 완료", description = "루틴 완료 API, 루틴 한 날짜 저장, 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "500", description = "기타 서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> completeTodayRoutine(@PathVariable("id") Long routineId) {
        try{
            routineService.completeTodayRoutine(routineId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataNotFoundException e) {
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.UNKNOWN.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
