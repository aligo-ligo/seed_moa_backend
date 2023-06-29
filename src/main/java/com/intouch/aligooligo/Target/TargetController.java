package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<List<Target>> getTargetList(@RequestParam Long id){
        try{
            return ResponseEntity.ok().body(targetService.getTargetList(id));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createTarget(HttpServletRequest request, @RequestBody Target req) {
        try {
            System.out.println(req);
            System.out.println(request);
            String email = null;
            String token = request.getHeader("Authorization");
            System.out.println(token);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                email = authentication.getName();
            }
            targetService.createTarget(email, req);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
