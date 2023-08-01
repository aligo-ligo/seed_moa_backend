package com.intouch.aligooligo.repository;

import com.intouch.aligooligo.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Integer> {
    List<Target> findByUserIdOrderByIdDesc(Integer userid);

}
