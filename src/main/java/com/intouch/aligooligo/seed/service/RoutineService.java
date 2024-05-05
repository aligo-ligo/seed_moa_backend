package com.intouch.aligooligo.seed.service;

import com.intouch.aligooligo.exception.DataNotFoundException;
import com.intouch.aligooligo.seed.controller.dto.request.UpdateSeedRequest;
import com.intouch.aligooligo.seed.domain.Routine;
import com.intouch.aligooligo.seed.domain.RoutineTimestamp;
import com.intouch.aligooligo.seed.repository.RoutineRepository;
import com.intouch.aligooligo.seed.repository.RoutineTimestampRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineTimestampRepository routineTimestampRepository;

    @Transactional
    public void updateSeed(Long routineId, UpdateSeedRequest updateSeedRequest) {
        Routine routines = routineRepository.findById(routineId)
                .orElseThrow(() -> new DataNotFoundException("can't find routine"));

        routines.updateRoutine(updateSeedRequest.getRoutineTitle());
        routineTimestampRepository.save(new RoutineTimestamp(LocalDate.now(), routines));
    }

}
