package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.seed.domain.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsBySeedIdAndUserId(Long seedId, Long userId);
    void deleteBySeedIdAndUserId(Long seedId, Long userId);

}
