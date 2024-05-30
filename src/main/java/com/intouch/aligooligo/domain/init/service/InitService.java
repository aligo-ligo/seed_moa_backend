package com.intouch.aligooligo.domain.init.service;

import com.intouch.aligooligo.domain.user.repository.UserRepository;
import com.intouch.aligooligo.domain.init.controller.dto.InitDataResponse;
import com.intouch.aligooligo.domain.seed.repository.SeedRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InitService {
    private UserRepository userRepository;
    private SeedRepository seedRepository;

    public InitDataResponse getInitData() {
        Long totalUserCount = userRepository.count();
        Long totalSeedCount = seedRepository.count();
        return new InitDataResponse(totalUserCount, totalSeedCount);
    }
}
