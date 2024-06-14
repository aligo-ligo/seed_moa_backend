package com.intouch.aligooligo.domain.member.controller;

import com.intouch.aligooligo.domain.member.entity.Role;
import com.intouch.aligooligo.domain.member.entity.Member;
import com.intouch.aligooligo.domain.member.repository.MemberRepository;
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
public class MemberController {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtProvider;
    @PostMapping
    public ResponseEntity<?> login() {
        Member member = memberRepository.findByEmail("admin").get();
        TokenInfo tokenInfo = jwtProvider.createToken(member.getEmail(), Role.MEMBER);

        return new ResponseEntity<>(new SignInResponse(tokenInfo, false), HttpStatus.OK);
    }
}
