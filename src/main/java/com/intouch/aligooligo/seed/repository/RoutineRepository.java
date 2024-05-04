package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.seed.domain.Routine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findBySeedId(Long seedId);
    @Transactional
    void deleteBySeedId(Long seedId);
}
