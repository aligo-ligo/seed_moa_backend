package com.intouch.aligooligo.seed.controller;


import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.DataNotFoundException;
import com.intouch.aligooligo.exception.ErrorMessage;
import com.intouch.aligooligo.exception.ErrorMessageDescription;
import com.intouch.aligooligo.seed.controller.dto.request.CreateSeedRequest;
import com.intouch.aligooligo.seed.controller.dto.request.UpdateSeedRequest;
import com.intouch.aligooligo.seed.controller.dto.response.MySeedDataResponse;
import com.intouch.aligooligo.seed.controller.dto.response.SeedDetailResponse;
import com.intouch.aligooligo.seed.service.SeedService;
import com.intouch.aligooligo.seed.controller.dto.response.SeedListResponse;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping(value="/api/seed")
@RestController
@Tag(name = "seed", description = "시드 관련 API")
public class SeedController {
    private final JwtTokenProvider jwtTokenProvider;
    private final SeedService seedService;

    @GetMapping
    @Operation(summary = "시드 리스트 조회", description = "시드 리스트 조회 API, 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SeedListResponse.class))),
            @ApiResponse(responseCode = "500", description = "유저 정보를 찾을 수 없을 때",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> getSeedList(
            HttpServletRequest request,
            @RequestParam Integer page,
            @RequestParam Integer size){
        try{
            SeedListResponse response = seedService.getSeedList(getUserEmail(request), page, size);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.UNKNOWN.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getUserEmail(HttpServletRequest request){
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        Claims claims = jwtTokenProvider.parseClaims(accessToken);
        return claims.getSubject();
    }


    @PostMapping
    @Operation(summary = "시드 리스트 생성", description = "시드 리스트 생성 API, 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "500", description = "유저 정보를 찾을 수 없을 때",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> createTarget(HttpServletRequest request, @RequestBody CreateSeedRequest createSeedRequest) {
        try {
            seedService.createSeed(getUserEmail(request), createSeedRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.UNKNOWN.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "시드 삭제", description = "시드 삭제 API, 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "500", description = "기타 서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> deleteSeed(@PathVariable("id") Long seedId){
        seedService.deleteSeed(seedId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "시드 디테일 조회", description = "시드 디테일 조회 API, 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SeedDetailResponse.class))),
            @ApiResponse(responseCode = "500", description = "db data 조회 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> detailTarget(@PathVariable("id") Long seedId){
        try {
            SeedDetailResponse detailResponse = seedService.getDetailSeed(seedId);
            return new ResponseEntity<>(detailResponse, HttpStatus.OK);
        } catch (DataNotFoundException e) {
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.UNKNOWN.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my")
    @Operation(summary = "마이 페이지 조회", description = "유저 이름과 이름, 스테이트 통계를 볼 수 있다. , 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MySeedDataResponse.class))),
            @ApiResponse(responseCode = "500", description = "기타 서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> getMyData(HttpServletRequest request) {
        try {
            MySeedDataResponse mySeedDataResponse = seedService.getMyData(getUserEmail(request));
            return new ResponseEntity<>(mySeedDataResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.UNKNOWN.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/like")
    @Operation(summary = "응원하기(좋아요) 증가/감소", description = "특정 seed의 좋아요를 증가/감소시킨다. 응원중인 씨앗에"
            + "재 요청이 왔을 때는 Http 200과 message로 \"이미 응원중인 씨앗입니다\" 문구가 출력. "
            + "이에 따라 재 요청 시 감소하도록 프론트 로직을 처리할 수 있다. , 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "1. 증가 성공\t\n 2. 이미 응원중인 씨앗일 때"),
            @ApiResponse(responseCode = "500", description = "기타 서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> increaseLike(@PathVariable("id") Long seedId, @RequestParam Boolean isIncreased) {
        try{
            if (isIncreased) {
                seedService.increaseLike(seedId);
            }
            else {
                seedService.decreaseLike(seedId);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.ALREADY_EXIST_LIKE.getDescription()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage(ErrorMessageDescription.UNKNOWN.getDescription()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
