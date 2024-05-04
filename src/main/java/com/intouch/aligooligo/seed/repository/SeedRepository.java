package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.seed.domain.Seed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeedRepository extends JpaRepository<Seed, Integer> {
    Page<Seed> findByUserIdOrderByIdDesc(Integer userId, Pageable pageable);
    boolean existsById(Integer targetId);
}
