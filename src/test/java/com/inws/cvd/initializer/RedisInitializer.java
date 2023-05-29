package com.inws.cvd.initializer;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

public class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    static {
        new GenericContainer("redis:7.0-alpine")
                .withExposedPorts(REDIS_PORT)
                .start();
    }

    @Override
    public void initialize(final @NotNull ConfigurableApplicationContext applicationContext) {
        applyProperties(applicationContext);
    }

    private void applyProperties(final ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.redis.host:" + REDIS_HOST,
                "spring.redis.port:" + REDIS_PORT
        ).applyTo(applicationContext);
    }

}
