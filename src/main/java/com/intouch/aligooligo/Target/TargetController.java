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
                targetService.createTarget(email, req);
                return ResponseEntity.ok().build();//ok
            }
            return ResponseEntity.status(403).build();//not auth
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();//server error
        }
    }

    public String checkJwtValidation(HttpServletRequest request){
        try {
            String token = request.getHeader("Authorization");
            if (token != null && jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getAuthentication(token).getName();
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
