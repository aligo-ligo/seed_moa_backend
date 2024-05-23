package com.intouch.aligooligo.seed.service;

import com.intouch.aligooligo.exception.DataNotFoundException;
import com.intouch.aligooligo.exception.ErrorMessageDescription;
import com.intouch.aligooligo.seed.controller.dto.request.UpdateSeedRequest;
import com.intouch.aligooligo.seed.domain.Routine;
import com.intouch.aligooligo.seed.domain.RoutineTimestamp;
import com.intouch.aligooligo.seed.domain.Seed;
import com.intouch.aligooligo.seed.repository.RoutineRepository;
import com.intouch.aligooligo.seed.repository.RoutineTimestampRepository;
import com.intouch.aligooligo.seed.repository.SeedRepository;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineTimestampRepository routineTimestampRepository;
    private final SeedRepository seedRepository;
    private final SeedService seedService;

    @Transactional
    public void updateSeed(Long routineId, UpdateSeedRequest updateSeedRequest) {
        Routine routines = routineRepository.findById(routineId)
                .orElseThrow(() -> {
                    log.error("RoutineService - updateSeed : can't find routine");
                    return new DataNotFoundException(ErrorMessageDescription.UNKNOWN.getDescription());
                });

        routines.updateRoutine(updateSeedRequest.getRoutineTitle());
    }

    @Transactional
    public void completeTodayRoutine(Long routineId) {
        Routine routines = routineRepository.findById(routineId)
                .orElseThrow(() -> {
                    log.error("RoutineService - completeTodayRoutine : can't find routine");
                    return new DataNotFoundException(ErrorMessageDescription.ROUTINE_NO_EXISTED.getDescription());
                });

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1);

        if (!routineTimestampRepository.existsByRoutineIdAndTimestampBetween(routineId, startOfDay, endOfDay)) {
            routineTimestampRepository.save(new RoutineTimestamp(LocalDateTime.now(), routines));

            Seed seed = seedRepository.findById(routines.getSeed().getId())
                    .orElseThrow(() -> {
                        log.error("RoutineService - completeTodayRoutine : can't find seed");
                        return new DataNotFoundException(ErrorMessageDescription.UNKNOWN.getDescription());
                    });

            Integer diffDays = Long.valueOf(ChronoUnit.DAYS.between(seed.getStartDate(), seed.getEndDate())).intValue() + 1;
            Integer routinesTotalCount = routineRepository.countBySeedId(seed.getId()) * diffDays;
            seedService.updateSeedState(routinesTotalCount, seed);
        }
    }

}
