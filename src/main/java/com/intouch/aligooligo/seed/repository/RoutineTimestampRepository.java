package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.seed.domain.RoutineTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineTimestampRepository extends JpaRepository<RoutineTimestamp, Long> {
    Integer countByRoutineId(Long routineId);
}
