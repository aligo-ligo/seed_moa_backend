package com.intouch.aligooligo.seed.controller;


import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import com.intouch.aligooligo.exception.ErrorMessage;
import com.intouch.aligooligo.seed.controller.dto.request.CreateSeedRequest;
import com.intouch.aligooligo.seed.controller.dto.request.UpdateSeedRequest;
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
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
            return ResponseEntity.ok().build();//ok
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
        try {
            seedService.deleteSeed(seedId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
//
//
//
//    @GetMapping("/detail")
//    public ResponseEntity<TargetDTO> detailTarget(@RequestParam Integer id){
//        try{
//            TargetDTO targetDTO = seedService.getDetailTarget(id);
//            if(targetDTO==null)
//                return ResponseEntity.internalServerError().build();
//            return ResponseEntity.ok().body(targetDTO);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
    @PatchMapping("/{id}")
    @Operation(summary = "시드 수정", description = "시드 수정 API, 인증된 사용자만 접근 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "500", description = "기타 서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> updateTarget(@PathVariable("id") Long seedId,
            @RequestBody UpdateSeedRequest updateSeedRequest) {
        try{
            SeedDetailResponse detailResponse = seedService.updateSeed(seedId, updateSeedRequest);
            return new ResponseEntity<>(detailResponse, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
