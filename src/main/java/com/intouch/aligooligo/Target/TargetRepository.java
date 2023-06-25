package com.intouch.aligooligo.Target;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Long> {
    List<Target> findAllById(Integer id);
}
