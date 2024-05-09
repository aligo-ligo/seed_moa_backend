package com.intouch.aligooligo.init.service;

import com.intouch.aligooligo.User.Repository.UserRepository;
import com.intouch.aligooligo.init.controller.dto.InitDataResponse;
import com.intouch.aligooligo.seed.repository.SeedRepository;
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
