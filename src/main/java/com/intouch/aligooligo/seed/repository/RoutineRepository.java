package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.seed.domain.Routine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {
    List<Routine> findBySeedId(Long seedId);
}
