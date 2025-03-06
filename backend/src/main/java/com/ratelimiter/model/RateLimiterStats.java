package com.ratelimiter.model;

import lombok.Data;

@Data
public class RateLimiterStats {
    private int currentTokens;
    private int capacity;
    private int refillRate;
    private long requestCount;
    private long allowedCount;
    private double successRate;
} 