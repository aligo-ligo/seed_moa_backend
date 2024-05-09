package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.seed.domain.RoutineTimestamp;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RoutineTimestampRepository extends JpaRepository<RoutineTimestamp, Long> {
    Integer countByRoutineId(Long routineId);

    Boolean existsByRoutineIdAndTimestamp(Long routineId, LocalDate today);
    @Transactional
    void deleteByRoutineId(Long routineId);
}
