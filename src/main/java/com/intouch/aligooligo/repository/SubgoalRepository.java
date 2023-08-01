package com.intouch.aligooligo.repository;

import com.intouch.aligooligo.entity.Subgoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubgoalRepository extends JpaRepository<Subgoal, Integer> {
    long countByTargetId(Integer id);
    List<Subgoal> findByTargetIdOrderByCompletedDateAsc(Integer id);
    List<Subgoal> findByTargetId(Integer id);
    List<Subgoal> findByTargetIdAndCompletedDateNotNullOrderByCompletedDateAsc(Integer id);

}