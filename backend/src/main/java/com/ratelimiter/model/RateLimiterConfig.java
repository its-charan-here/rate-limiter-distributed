package com.ratelimiter.model;

import lombok.Data;

@Data
public class RateLimiterConfig {
    private String clientId;
    private Algorithm algorithm;
    private int capacity;
    private int refillRate;
    private long windowSizeMs;
} 