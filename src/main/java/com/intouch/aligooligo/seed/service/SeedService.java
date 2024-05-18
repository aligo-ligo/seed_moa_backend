package com.intouch.aligooligo.seed.service;

import com.intouch.aligooligo.exception.DataNotFoundException;
import com.intouch.aligooligo.exception.ErrorMessageDescription;
import com.intouch.aligooligo.seed.controller.dto.RoutineInfo;
import com.intouch.aligooligo.seed.controller.dto.request.CreateSeedRequest;
import com.intouch.aligooligo.seed.controller.dto.response.CheeringUser;
import com.intouch.aligooligo.seed.controller.dto.response.MySeedDataResponse;
import com.intouch.aligooligo.seed.controller.dto.response.MySeedDataResponse.StateStatistics;
import com.intouch.aligooligo.seed.controller.dto.response.SeedDetailResponse;
import com.intouch.aligooligo.seed.controller.dto.response.SeedDetailResponse.RoutineDetail;
import com.intouch.aligooligo.seed.controller.dto.response.SeedListResponse;
import com.intouch.aligooligo.seed.domain.Cheering;
import com.intouch.aligooligo.seed.controller.dto.response.SeedSharedResponse;
import com.intouch.aligooligo.seed.controller.dto.response.SeedSharedResponse.SharedRoutineDetail;
import com.intouch.aligooligo.seed.domain.Routine;
import com.intouch.aligooligo.seed.domain.Seed;
import com.intouch.aligooligo.seed.domain.SeedState;
import com.intouch.aligooligo.seed.repository.CheeringRepository;
import com.intouch.aligooligo.seed.repository.RoutineRepository;
import com.intouch.aligooligo.seed.repository.RoutineTimestampRepository;
import com.intouch.aligooligo.seed.repository.SeedRepository;
import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.User.Repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final CheeringRepository cheeringRepository;
    private final static Integer PERCENT = 100;

    @Value("${urlPrefix}")
    private String urlPrefix;

    public SeedListResponse getSeedList(String email, Integer page, Integer size){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> {
                    log.error("SeedService - getSeedList : can't find userEmail");
                    return new UsernameNotFoundException(ErrorMessageDescription.UNKNOWN.getDescription());
                });
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Seed> seedList = seedRepository.findByUserIdOrderByIdDesc(user.getId(), pageRequest);

        List<Integer> completedRoutineCountList = new ArrayList<>();
        List<List<Routine>> routinesList = new ArrayList<>();
        long cheeringCount = 0;

        for (Seed seed : seedList) {
            List<Routine> routines = routineRepository.findBySeedId(seed.getId());
            cheeringCount = cheeringRepository.countBySeedId(seed.getId());
            Integer completedRoutineCount = getCompletedRoutineCount(routines);
            routinesList.add(routines);
            completedRoutineCountList.add(completedRoutineCount);
        }

        SeedListResponse listResponse = new SeedListResponse();
        listResponse.updateSeedList(seedList, routinesList, cheeringCount, completedRoutineCountList);
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
                .orElseThrow(() -> {
                    log.error("SeedService - createSeed : can't find userEmail");
                    return new UsernameNotFoundException(ErrorMessageDescription.UNKNOWN.getDescription());
                });

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.parse(createSeedRequest.getEndDate());
        Seed seed = seedRepository.save(Seed.builder().startDate(startDate).endDate(endDate)
                .seed(createSeedRequest.getSeed()).state(SeedState.SEED.name()).user(user).build());

        for (RoutineInfo routineTitle : createSeedRequest.getRoutines()) {
            routineRepository.save(Routine.builder().title(routineTitle.getValue()).seed(seed).build());
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
                .orElseThrow(() -> {
                    log.error("SeedService - getDetailSeed : can't find seed");
                    return new DataNotFoundException(ErrorMessageDescription.SEED_NOT_FOUND.getDescription());
                });
        List<Routine> routines = routineRepository.findBySeedId(seedId);
        List<CheeringUser> cheeringUserNameList = cheeringRepository.findBySeedId(seedId).stream()
                .map(cheering -> new CheeringUser(cheering.getUser().getNickName()))
                .toList();
        LocalDate today = LocalDate.now();

        List<RoutineDetail> routineDetails = getRoutineDetails(routines, today);

        Integer completedRoutineCount = getCompletedRoutineCount(routines);

        return SeedDetailResponse.builder().id(seed.getId()).seedName(seed.getSeed())
                .startDate(String.valueOf(seed.getStartDate())).endDate(String.valueOf(seed.getEndDate()))
                .completedRoutineCount(completedRoutineCount).routineDetails(routineDetails)
                .seedState(seed.getState()).cheeringUserList(cheeringUserNameList).build();
    }

    public SeedSharedResponse getSharedSeed(Long seedId) {
        Seed seed = seedRepository.findById(seedId)
                .orElseThrow(() -> {
                    log.error("SeedService - getDetailSeed : can't find seed");
                    return new DataNotFoundException(ErrorMessageDescription.SEED_NOT_FOUND.getDescription());
                });
        List<Routine> routines = routineRepository.findBySeedId(seedId);
        List<CheeringUser> cheeringUserNameList = cheeringRepository.findBySeedId(seedId).stream()
                .map(cheering -> new CheeringUser(cheering.getUser().getNickName()))
                .toList();
        LocalDate today = LocalDate.now();

        List<SharedRoutineDetail> sharedRoutineDetails = getSharedRoutineDetails(routines, today);

        Integer completedRoutineCount = getCompletedRoutineCount(routines);

        return SeedSharedResponse.builder().id(seedId).seedName(seed.getSeed())
                .startDate(String.valueOf(seed.getStartDate())).endDate(String.valueOf(seed.getEndDate()))
                .completedRoutineCount(completedRoutineCount).routineDetails(sharedRoutineDetails)
                .seedState(seed.getState()).cheeringUserList(cheeringUserNameList).build();
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

    private List<SharedRoutineDetail> getSharedRoutineDetails(List<Routine> routines, LocalDate today) {
        List<SharedRoutineDetail> sharedRoutineDetails = new ArrayList<>();
        SharedRoutineDetail sharedRoutineDetail;
        for (Routine routine : routines) {
            if (routineTimestampRepository.existsByRoutineIdAndTimestamp(routine.getId(), today)) {
                sharedRoutineDetail = SharedRoutineDetail.builder().routineId(routine.getId())
                        .routineTitle(routine.getTitle()).build();
            }
            else {
                sharedRoutineDetail = SharedRoutineDetail.builder().routineId(routine.getId())
                        .routineTitle(routine.getTitle()).build();
            }
            sharedRoutineDetails.add(sharedRoutineDetail);
        }

        return sharedRoutineDetails;
    }

    public MySeedDataResponse getMyData(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("SeedService - getMyData : can't find userEmail");
                    return new UsernameNotFoundException(ErrorMessageDescription.UNKNOWN.getDescription());
                });
        List<Seed> seeds = seedRepository.findByUserId(user.getId());
        Map<String, Long> seedStateCount = seeds.stream().collect(
                Collectors.groupingBy(Seed::getState, Collectors.counting()));

        for (SeedState seedState : SeedState.values()) {
            seedStateCount.putIfAbsent(seedState.name(), 0L);
        }

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

    public void updateSeedState(Integer routinesTotalCount, Seed seed) {
        List<Routine> routines = routineRepository.findBySeedId(seed.getId());
        Integer routinesCompletedCount = getCompletedRoutineCount(routines);
        List<Integer> statusBoundaries = new ArrayList<>();

        for (SeedState seedState : SeedState.values()) {
            statusBoundaries.add(seedState.getBoundary() * routinesTotalCount / PERCENT);
        }

        for (int i= 0; i < statusBoundaries.size(); i++) {
            if (routinesCompletedCount <= statusBoundaries.get(i)) {
                seed.updateSeedState(SeedState.values()[i].name());
                break;
            }
        }
    }

    @Transactional
    public void increaseLike(Long seedId) {
        Seed seed = seedRepository.findById(seedId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessageDescription.SEED_NOT_FOUND.getDescription()));
        if (cheeringRepository.existsBySeedIdAndUserId(seed.getId(), seed.getUser().getId())) {
            throw new IllegalArgumentException("이미 응원중인 씨앗입니다.");
        }
        cheeringRepository.save(new Cheering(seed, seed.getUser()));
    }

    @Transactional
    public void decreaseLike(Long seedId) {
        Seed seed = seedRepository.findById(seedId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessageDescription.SEED_NOT_FOUND.getDescription()));
        cheeringRepository.deleteBySeedIdAndUserId(seed.getId(), seed.getUser().getId());
    }
}