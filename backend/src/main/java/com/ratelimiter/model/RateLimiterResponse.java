package com.ratelimiter.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateLimiterResponse {
    private boolean allowed;
    private int currentTokens;
    private int capacity;
    private int refillRate;
} 