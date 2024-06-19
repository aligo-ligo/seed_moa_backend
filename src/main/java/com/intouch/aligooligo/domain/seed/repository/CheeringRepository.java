package com.intouch.aligooligo.domain.seed.repository;

import com.intouch.aligooligo.domain.seed.domain.Cheering;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheeringRepository extends JpaRepository<Cheering, Long> {
    boolean existsBySeedIdAndMemberId(Long seedId, Long memberId);
    long countBySeedId(Long seedId);
    void deleteBySeedIdAndMemberId(Long seedId, Long memberId);
    void deleteBySeedId(Long seedId);
    List<Cheering> findBySeedId(Long seedId);

}
