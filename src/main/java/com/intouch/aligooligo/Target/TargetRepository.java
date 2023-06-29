package com.intouch.aligooligo.Target;

import com.intouch.aligooligo.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetRepository extends JpaRepository<Target, Long> {
    List<Target> findAllByUserId(Long userid);

}
