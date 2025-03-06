package com.ratelimiter.service;

import com.ratelimiter.model.RateLimiterResponse;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TokenBucketRateLimiter {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_REFILL_RATE = 2;

    private static class TokenBucket {
        private final AtomicInteger tokens;
        private final int capacity;
        private final int refillRate;
        private long lastRefillTimestamp;

        public TokenBucket(int capacity, int refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = new AtomicInteger(capacity);
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume() {
            refill();
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTimestamp;
            if (timePassed < 1000) { // Less than 1 second passed
                return;
            }

            int tokensToAdd = (int) (timePassed / 1000) * refillRate;
            if (tokensToAdd > 0) {
                int newTokens = Math.min(capacity, tokens.get() + tokensToAdd);
                tokens.set(newTokens);
                lastRefillTimestamp = now;
            }
        }

        public int getCurrentTokens() {
            refill();
            return tokens.get();
        }

        public int getCapacity() {
            return capacity;
        }

        public int getRefillRate() {
            return refillRate;
        }
    }

    public RateLimiterResponse check(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
            k -> new TokenBucket(DEFAULT_CAPACITY, DEFAULT_REFILL_RATE));

        boolean allowed = bucket.tryConsume();
        
        return RateLimiterResponse.builder()
            .allowed(allowed)
            .currentTokens(bucket.getCurrentTokens())
            .capacity(bucket.getCapacity())
            .refillRate(bucket.getRefillRate())
            .build();
    }

    public void resetBucket(String clientId, int capacity, int refillRate) {
        buckets.put(clientId, new TokenBucket(capacity, refillRate));
    }
}
 