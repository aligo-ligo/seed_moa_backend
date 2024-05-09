package com.intouch.aligooligo.seed.service;

import com.intouch.aligooligo.exception.DataNotFoundException;
import com.intouch.aligooligo.seed.controller.dto.request.UpdateSeedRequest;
import com.intouch.aligooligo.seed.domain.Routine;
import com.intouch.aligooligo.seed.domain.RoutineTimestamp;
import com.intouch.aligooligo.seed.domain.Seed;
import com.intouch.aligooligo.seed.repository.RoutineRepository;
import com.intouch.aligooligo.seed.repository.RoutineTimestampRepository;
import com.intouch.aligooligo.seed.repository.SeedRepository;
import java.time.LocalDate;

import java.time.Period;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineTimestampRepository routineTimestampRepository;
    private final SeedRepository seedRepository;
    private final SeedService seedService;

    @Transactional
    public void updateSeed(Long routineId, UpdateSeedRequest updateSeedRequest) {
        Routine routines = routineRepository.findById(routineId)
                .orElseThrow(() -> new DataNotFoundException("can't find routine"));

        routines.updateRoutine(updateSeedRequest.getRoutineTitle());
    }

    @Transactional
    public void completeTodayRoutine(Long routineId) {
        Routine routines = routineRepository.findById(routineId)
                .orElseThrow(() -> new DataNotFoundException("can't find routine"));

        if (!routineTimestampRepository.existsByRoutineIdAndTimestamp(routineId, LocalDate.now())) {
            routineTimestampRepository.save(new RoutineTimestamp(LocalDate.now(), routines));

            Seed seed = seedRepository.findById(routines.getSeed().getId())
                    .orElseThrow(() -> new DataNotFoundException("can't find seed"));

            Integer diffDays = Long.valueOf(ChronoUnit.DAYS.between(seed.getStartDate(), seed.getEndDate())).intValue();
            Integer routinesTotalCount = routineRepository.countBySeedId(seed.getId()) * diffDays;
            seedService.updateSeedState(routinesTotalCount, seed);
        }
    }

}
