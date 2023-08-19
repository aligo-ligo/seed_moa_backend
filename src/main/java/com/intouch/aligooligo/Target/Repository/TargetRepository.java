package com.intouch.aligooligo.Target.Repository;

import com.intouch.aligooligo.Target.Entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Integer> {
    List<Target> findByUserIdOrderByIdDesc(Integer userid);

    boolean existsById(Integer targetId);
}
