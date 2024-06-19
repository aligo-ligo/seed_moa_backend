
package com.intouch.aligooligo.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainer implements BeforeAllCallback {
    private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
    private static final int REDIS_PORT = 6379;
    private GenericContainer redis;

    @Override
    public void beforeAll(ExtensionContext context) {
        redis = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT);
        redis.start();
        System.setProperty("spring.data.redis.host", redis.getHost());
        System.out.println(redis.getHost());
        System.out.println(String.valueOf(redis.getMappedPort(REDIS_PORT)));
        System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(REDIS_PORT
        )));
    }
}
