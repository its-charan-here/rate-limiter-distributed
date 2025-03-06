package com.ratelimiter.model;

import lombok.Data;

@Data
public class RateLimiterRequest {
    private String clientId;
    private int tps;
    private int capacity;
    private int refillRate;
} 