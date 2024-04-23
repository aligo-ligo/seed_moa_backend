package com.intouch.aligooligo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TestConfig {
    @Value("${redirectUrl}")
    private String redirectUrl;
    @Value("${clientId}")
    private String clientId;

    @Value("${clientSecret}")
    private String clientSecret;

    @Value("${secretKey}")
    private String secretKey;

    @Value("${corsAttr}")
    private String corsAttr;

    @Value("${spring.profiles.active}")
    private String active;

    @Bean
    public void testConfig() {
        log.info(redirectUrl);
        log.info(clientId);
        log.info(clientSecret);
        log.info(secretKey);
        log.info(corsAttr);
        log.info(active);
    }
}
