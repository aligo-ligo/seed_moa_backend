package com.intouch.aligooligo.repository;


import com.intouch.aligooligo.entity.KakaoUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoUserRepository extends JpaRepository<KakaoUser,Integer> {
    Optional<KakaoUser> findByEmail(String email);
}
