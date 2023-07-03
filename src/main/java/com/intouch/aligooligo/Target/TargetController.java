package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RequestMapping(value="/target")
@RestController
@CrossOrigin
public class TargetController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TargetService targetService;

    @GetMapping("/list")
    public ResponseEntity<List<TargetDTO>> getTargetList(HttpServletRequest request){
        try{
            String email = checkJwtValidation(request);
            if(email.equals("Auth expired"))
                return ResponseEntity.status(401).build();
            List<TargetDTO> list = targetService.getTargetList(email);
            return ResponseEntity.ok().body(list);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createTarget(HttpServletRequest request, @RequestBody Target req) {
        try {
            String email = checkJwtValidation(request);
            if (email != null) {
                boolean created = targetService.createTarget(email, req);
                if(created)
                    return ResponseEntity.ok().build();//ok
                return ResponseEntity.internalServerError().build();//server error
            }
            return ResponseEntity.status(401).build();//not auth
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();//server error
        }
    }

    public String checkJwtValidation(HttpServletRequest request){
        try {
            String token = request.getHeader("Authorization");
            System.out.println(token);
            if (token != null) {
                String req_token = token.substring(7);
                System.out.println(jwtTokenProvider.validateToken(token));
                if(jwtTokenProvider.validateToken(req_token)) {
                    return jwtTokenProvider.getAuthentication(req_token).getName();
                }
            }
            System.out.println(jwtTokenProvider.validateToken(token));
            return "Auth expired";
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<TargetDTO> detailTarget(@RequestParam Long targetId){
        try{
            TargetDTO targetDTO = targetService.getDetailTarget(targetId);
            if(targetDTO==null)
                return ResponseEntity.internalServerError().build();
            return ResponseEntity.ok().body(targetDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateTarget(@RequestBody TargetUpdateReq req){
        try{
            boolean updated = targetService.updateTarget(req);
            if(updated)
                return ResponseEntity.ok().body("updating is completed");
            return ResponseEntity.internalServerError().build();
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/share")
    public ResponseEntity<String> ShareUrl(@RequestParam Long targetId){
        try {
            String shortUrl = targetService.shareUrl(targetId);
            System.out.println(shortUrl);
            return ResponseEntity.ok().body(shortUrl);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/result")
    public ResponseEntity<TargetDTO> resultTargetPage(@RequestParam String shortUrl){
        try{
            TargetDTO targetDTO = targetService.resultTargetPage(shortUrl);
            if(targetDTO ==null)
                return ResponseEntity.internalServerError().build();
            return ResponseEntity.ok().body(targetDTO);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/vote")
    public ResponseEntity<HttpStatus> voteTarget(@RequestParam(value = "targetId") Long id, @RequestParam(value = "success") boolean success){
        try{
            boolean voted = targetService.voteTarget(id, success);
            if(voted)
                return ResponseEntity.ok().build();//ok
            return ResponseEntity.internalServerError().build();//server error
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();//server error
        }
    }
}
