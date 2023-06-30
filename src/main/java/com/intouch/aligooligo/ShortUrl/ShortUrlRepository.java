package com.intouch.aligooligo.ShortUrl;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    boolean existsByTargetId(Long id);
    boolean existsByShortUrl(String shortUrl);
    ShortUrl findByTargetId(Long id);
    ShortUrl findByShortUrl(String shortUrl);
}
