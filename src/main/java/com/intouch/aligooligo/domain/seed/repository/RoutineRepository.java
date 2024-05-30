package com.intouch.aligooligo.domain.seed.repository;

import com.intouch.aligooligo.domain.seed.domain.Routine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findBySeedId(Long seedId);
    Integer countBySeedId(Long seedId);
    @Transactional
    void deleteBySeedId(Long seedId);
}
