package com.intouch.aligooligo.User.Controller;

import com.intouch.aligooligo.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.User.Entity.Role;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
import com.intouch.aligooligo.auth.dto.TokenInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping(value="/users")
@RestController
public class UserController {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    @PostMapping
    public ResponseEntity<?> login(@RequestParam("email") String admin) {
        User user = userRepository.findByEmail(admin).get();
        TokenInfo tokenInfo = null;
        if(user.getEmail().equals(admin)) {
            tokenInfo = jwtProvider.createToken(admin, Role.USER);
        }
        return new ResponseEntity<>(tokenInfo, HttpStatus.OK);
    }
}
