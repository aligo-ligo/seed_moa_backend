package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.seed.domain.Seed;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeedRepository extends JpaRepository<Seed, Long> {
    Page<Seed> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
    List<Seed> findByUserId(Long userId);
    boolean existsById(Integer targetId);
}
