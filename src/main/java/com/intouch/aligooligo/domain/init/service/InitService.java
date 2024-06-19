package com.intouch.aligooligo.domain.init.service;

import com.intouch.aligooligo.domain.member.repository.MemberRepository;
import com.intouch.aligooligo.domain.init.controller.dto.InitDataResponse;
import com.intouch.aligooligo.domain.seed.repository.SeedRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InitService {
    private MemberRepository memberRepository;
    private SeedRepository seedRepository;

    public InitDataResponse getInitData() {
        Long totalUserCount = memberRepository.count();
        Long totalSeedCount = seedRepository.count();
        return new InitDataResponse(totalUserCount, totalSeedCount);
    }
}
