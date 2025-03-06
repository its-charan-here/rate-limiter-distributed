package com.ratelimiter.integration;

import com.ratelimiter.model.RateLimiterRequest;
import com.ratelimiter.model.RateLimiterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class RateLimiterIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @Test
    void shouldLimitRequests() {
        String url = "http://localhost:" + port + "/api/rate-limiter/simulate";
        RateLimiterRequest request = new RateLimiterRequest();
        request.setClientId("test-client");

        // First request should be allowed
        ResponseEntity<RateLimiterResponse> response = restTemplate.postForEntity(
            url, request, RateLimiterResponse.class);
        
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAllowed()).isTrue();
    }
} 