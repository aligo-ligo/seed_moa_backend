package com.intouch.aligooligo.Target.Repository;

import com.intouch.aligooligo.Target.Entity.Target;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Integer> {
//    Page<Target> findByUserIdOrderByIdDesc(Integer userId, Pageable pageable);
    List<Target> findByUserIdOrderByIdDesc(Integer userId);

    Integer countByUserId(Integer userId);

    boolean existsById(Integer targetId);
}
