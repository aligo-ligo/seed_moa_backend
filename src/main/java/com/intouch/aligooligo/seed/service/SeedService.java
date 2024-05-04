package com.intouch.aligooligo.seed.service;

import com.intouch.aligooligo.seed.controller.dto.request.CreateSeedRequest;
import com.intouch.aligooligo.seed.controller.dto.request.UpdateSeedRequest;
import com.intouch.aligooligo.seed.controller.dto.response.SeedDetailResponse;
import com.intouch.aligooligo.seed.controller.dto.response.SeedListResponse;
import com.intouch.aligooligo.seed.domain.Routine;
import com.intouch.aligooligo.seed.domain.Seed;
import com.intouch.aligooligo.seed.domain.SeedState;
import com.intouch.aligooligo.seed.repository.RoutineRepository;
import com.intouch.aligooligo.seed.repository.RoutineTimestampRepository;
import com.intouch.aligooligo.seed.repository.SeedRepository;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(()->new UsernameNotFoundException("can't get seedList : can't find userEmail"));
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

    @Transactional
    public void createSeed(String userEmail, CreateSeedRequest createSeedRequest) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("can't get seedList : can't find userEmail"));

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.parse(createSeedRequest.getEndDate());
        Seed seed = seedRepository.save(Seed.builder().startDate(startDate).endDate(endDate)
                .seed(createSeedRequest.getSeed()).state(SeedState.SEED.name()).user(user).build());

        for (String routineTitle : createSeedRequest.getRoutines()) {
            routineRepository.save(Routine.builder().title(routineTitle).seed(seed).build());
        }
    }

    @Transactional
    public void updateSeed(Long seedId, UpdateSeedRequest updateSeedRequest) {
        List<Routine> routines = routineRepository.findBySeedId(seedId);

        for (Routine routine : routines) {
            if (routine.getTitle().equals(updateSeedRequest.getOldRoutineTitle())) {
                routine.updateRoutine(updateSeedRequest.getNewRoutineTitle());
            }
        }
    }

    @Transactional
    public void deleteSeed(Long seedId) {
        List<Routine> routines = routineRepository.findBySeedId(seedId);
        for (Routine routine : routines) {
            routineTimestampRepository.deleteByRoutineId(routine.getId());
        }
        routineRepository.deleteBySeedId(seedId);
        seedRepository.deleteById(seedId);
    }

    public SeedDetailResponse getDetailSeed(Long seedId) {
        Seed seed = seedRepository.findById(seedId)
                .orElseThrow(() -> new IllegalArgumentException("doesn't find seed"));
        List<Routine> routines = routineRepository.findBySeedId(seedId);
        LocalDate today = LocalDate.now();

        Map<String, Boolean> responseRoutines = completedTodayRoutines(routines, today);
        Integer completedRoutineCount = getCompletedRoutineCount(routines);

        return SeedDetailResponse.builder().seed(seed.getSeed()).startDate(String.valueOf(seed.getStartDate()))
                .endDate(String.valueOf(seed.getEndDate())).completedRoutineCount(completedRoutineCount)
                .completedTodayRoutines(responseRoutines).state(seed.getState()).build();
    }

    private Map<String, Boolean> completedTodayRoutines(List<Routine> routines, LocalDate today) {
        Map<String, Boolean> responseRoutines = new HashMap<>();

        for (Routine routine : routines) {
            if (routineTimestampRepository.existsByRoutineIdAndTimestamp(routine.getId(), today)) {
                responseRoutines.put(routine.getTitle(), true);
            }
            else {
                responseRoutines.put(routine.getTitle(), false);
            }
        }

        return responseRoutines;
    }


}