package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.seed.domain.Cheering;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheeringRepository extends JpaRepository<Cheering, Long> {
    boolean existsBySeedIdAndUserId(Long seedId, Long userId);
    long countBySeedId(Long seedId);
    void deleteBySeedIdAndUserId(Long seedId, Long userId);
    void deleteBySeedId(Long seedId);
    List<Cheering> findBySeedId(Long seedId);

}
