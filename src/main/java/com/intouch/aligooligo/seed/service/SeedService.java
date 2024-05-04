package com.intouch.aligooligo.seed.service;

import com.intouch.aligooligo.seed.controller.dto.response.SeedListResponse;
import com.intouch.aligooligo.seed.domain.Routine;
import com.intouch.aligooligo.seed.domain.Seed;
import com.intouch.aligooligo.seed.repository.RoutineRepository;
import com.intouch.aligooligo.seed.repository.RoutineTimestampRepository;
import com.intouch.aligooligo.seed.repository.SeedRepository;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeedService {
    private final SeedRepository seedRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final RoutineTimestampRepository routineTimestampRepository;

    @Value("${urlPrefix}")
    private String urlPrefix;

    public SeedListResponse getSeedList(String email, Integer page, Integer size){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("can't get targetList : can't find userEmail"));
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Seed> seedList = seedRepository.findByUserIdOrderByIdDesc(user.getId(), pageRequest);
        List<Integer> completedRoutineCountList = new ArrayList<>();

        for (Seed seed : seedList) {
            List<Routine> routines = routineRepository.findBySeedId(seed.getId());
            Integer completedRoutineCount = getCompletedRoutineCount(routines);
            completedRoutineCountList.add(completedRoutineCount);
        }

        SeedListResponse listResponse = new SeedListResponse();
        listResponse.updateSeedList(seedList, completedRoutineCountList);
        listResponse.updatePages(seedList);

        return listResponse;
    }

    private Integer getCompletedRoutineCount(List<Routine> routines) {
        Integer completedRoutineCount = 0;
        for (Routine routine : routines) {
            completedRoutineCount += routineTimestampRepository.countByRoutineId(routine.getId());
        }
        return completedRoutineCount;
    }


}