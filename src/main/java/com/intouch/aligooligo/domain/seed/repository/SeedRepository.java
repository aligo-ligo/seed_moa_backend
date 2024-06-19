package com.intouch.aligooligo.domain.seed.repository;

import com.intouch.aligooligo.domain.seed.domain.Seed;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeedRepository extends JpaRepository<Seed, Long> {
    Page<Seed> findByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);
    List<Seed> findByMemberId(Long memberId);
    boolean existsById(Integer targetId);
}
