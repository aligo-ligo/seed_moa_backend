package com.intouch.aligooligo.seed.service;

import com.intouch.aligooligo.seed.controller.dto.request.CreateSeedRequest;
import com.intouch.aligooligo.seed.controller.dto.request.UpdateSeedRequest;
import com.intouch.aligooligo.seed.controller.dto.response.MySeedDataResponse;
import com.intouch.aligooligo.seed.controller.dto.response.MySeedDataResponse.StateStatistics;
import com.intouch.aligooligo.seed.controller.dto.response.SeedDetailResponse;
import com.intouch.aligooligo.seed.controller.dto.response.SeedDetailResponse.RoutineDetail;
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
import java.util.stream.Collectors;
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
        List<List<Routine>> routinesList = new ArrayList<>();


        for (Seed seed : seedList) {
            List<Routine> routines = routineRepository.findBySeedId(seed.getId());
            Integer completedRoutineCount = getCompletedRoutineCount(routines);
            routinesList.add(routines);
            completedRoutineCountList.add(completedRoutineCount);
        }

        SeedListResponse listResponse = new SeedListResponse();
        listResponse.updateSeedList(seedList, routinesList, completedRoutineCountList);
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

        List<RoutineDetail> routineDetails = getRoutineDetails(routines, today);

        Integer completedRoutineCount = getCompletedRoutineCount(routines);

        return SeedDetailResponse.builder().seed(seed.getSeed()).startDate(String.valueOf(seed.getStartDate()))
                .endDate(String.valueOf(seed.getEndDate())).completedRoutineCount(completedRoutineCount)
                .routineDetails(routineDetails).state(seed.getState()).build();
    }

    private List<RoutineDetail> getRoutineDetails(List<Routine> routines, LocalDate today) {
        List<RoutineDetail> routineDetails = new ArrayList<>();
        RoutineDetail routineDetail;
        for (Routine routine : routines) {
            if (routineTimestampRepository.existsByRoutineIdAndTimestamp(routine.getId(), today)) {
                routineDetail = RoutineDetail.builder().routineId(routine.getId())
                        .routineTitle(routine.getTitle()).completedRoutineToday(true).build();
            }
            else {
                routineDetail = RoutineDetail.builder().routineId(routine.getId())
                        .routineTitle(routine.getTitle()).completedRoutineToday(false).build();
            }
            routineDetails.add(routineDetail);
        }

        return routineDetails;
    }

    public MySeedDataResponse getMyData(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("can't get myData : can't find userEmail"));
        List<Seed> seeds = seedRepository.findByUserId(user.getId());
        Map<String, Long> seedStateCount = seeds.stream().collect(
                Collectors.groupingBy(Seed::getState, Collectors.counting()));

        return convertToMySeedDataResponse(user, seedStateCount);
    }

    private MySeedDataResponse convertToMySeedDataResponse(User user, Map<String, Long> seedStateCount) {
        String email = user.getEmail();
        String name = user.getNickName();
        List<StateStatistics> stateStatisticsList = new ArrayList<>();

        for (String seedState : seedStateCount.keySet()) {
            StateStatistics stateStatistics = new StateStatistics(seedState, seedStateCount.get(seedState));
            stateStatisticsList.add(stateStatistics);
        }

        return new MySeedDataResponse(email, name, stateStatisticsList);
    }


}