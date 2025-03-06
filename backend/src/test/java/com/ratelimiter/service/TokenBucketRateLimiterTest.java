package com.ratelimiter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class TokenBucketRateLimiterTest {

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    private TokenBucketRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new TokenBucketRateLimiter(
            redis.getHost(),
            redis.getFirstMappedPort(),
            10,
            2
        );
    }

    @Test
    void shouldAllowRequestsWithinCapacity() {
        String clientId = "test-client";
        
        // Should allow first 10 requests
        for (int i = 0; i < 10; i++) {
            assertThat(rateLimiter.isAllowed(clientId))
                .as("Request %d should be allowed", i + 1)
                .isTrue();
        }
        
        // Should deny next request
        assertThat(rateLimiter.isAllowed(clientId))
            .as("Request exceeding capacity should be denied")
            .isFalse();
    }

    @Test
    void shouldRefillTokens() throws InterruptedException {
        String clientId = "test-client";
        
        // Use all tokens
        for (int i = 0; i < 10; i++) {
            rateLimiter.isAllowed(clientId);
        }
        
        // Wait for refill
        Thread.sleep(1000);
        
        // Should have 2 new tokens (refill rate = 2)
        assertThat(rateLimiter.isAllowed(clientId)).isTrue();
        assertThat(rateLimiter.isAllowed(clientId)).isTrue();
        assertThat(rateLimiter.isAllowed(clientId)).isFalse();
    }

    @Test
    void healthChecksShouldWork() {
        assertThat(rateLimiter.ping()).isTrue();
        assertThat(rateLimiter.deepPing()).isTrue();
    }
} 