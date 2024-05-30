package com.intouch.aligooligo.domain.user.controller;

import com.intouch.aligooligo.domain.user.entity.Role;
import com.intouch.aligooligo.domain.user.entity.User;
import com.intouch.aligooligo.domain.user.repository.UserRepository;
import com.intouch.aligooligo.global.Jwt.JwtTokenProvider;
import com.intouch.aligooligo.domain.auth.dto.SignInResponse;
import com.intouch.aligooligo.domain.auth.dto.TokenInfo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping(value="/users")
@RestController
public class UserController {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    @PostMapping
    public ResponseEntity<?> login() {
        User user = userRepository.findByEmail("admin").get();
        TokenInfo tokenInfo = jwtProvider.createToken(user.getEmail(), Role.USER);

        return new ResponseEntity<>(new SignInResponse(tokenInfo, false), HttpStatus.OK);
    }
}
