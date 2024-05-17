package com.intouch.aligooligo.seed.repository;

import com.intouch.aligooligo.User.Entity.User;
import com.intouch.aligooligo.seed.domain.Cheering;
import com.intouch.aligooligo.seed.domain.Seed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheeringRepository extends JpaRepository<Cheering, Long> {
    boolean existsBySeedAndUser(Seed seed, User user);
    void deleteBySeedAndUser(Seed seed, User user);

}
