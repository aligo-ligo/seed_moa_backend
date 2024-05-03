package com.intouch.aligooligo.token;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    boolean existsByToken(String accessToken);
    void deleteByToken(String accessToken);
}
