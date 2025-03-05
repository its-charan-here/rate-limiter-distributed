package com.ratelimiter;

import redis.clients.jedis.Jedis;
import java.time.Instant;
import java.util.List;

/**
 * A distributed rate limiter implementation using the Token Bucket algorithm.
 * This implementation uses Redis as a backend storage to provide distributed rate limiting
 * across multiple instances of an application.
 *
 * The Token Bucket algorithm works by:
 * 1. Maintaining a bucket of tokens
 * 2. Each request consumes one token
 * 3. Tokens are refilled at a specified rate
 * 4. Requests are rejected when the bucket is empty
 *
 * This implementation is thread-safe and uses Redis transactions to ensure atomic operations.
 */
public class TokenBucketRateLimiter {
    private final Jedis jedis;
    private final int capacity;
    private final int refillRate;
    private final String keyPrefix;
    private static final String TOKENS_KEY = "tokens";
    private static final String LAST_UPDATE_KEY = "last_update";

    /**
     * Constructs a new TokenBucketRateLimiter with the specified parameters.
     *
     * @param jedis The Redis client instance to use for storage
     * @param capacity The maximum number of tokens the bucket can hold
     * @param refillRate The number of tokens to add per second
     */
    public TokenBucketRateLimiter(Jedis jedis, int capacity, int refillRate) {
        this.jedis = jedis;
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.keyPrefix = "rate_limiter:";
    }

    /**
     * Checks if a request from the specified client is allowed under the rate limit.
     *
     * @param clientId The unique identifier for the client making the request
     * @return true if the request is allowed, false if it should be rejected
     */
    public boolean isAllowed(String clientId) {
        String key = getKey(clientId);
        return tryConsume(key);
    }

    /**
     * Generates the Redis key for a specific client.
     *
     * @param clientId The client identifier
     * @return The complete Redis key for the client
     */
    private String getKey(String clientId) {
        return keyPrefix + clientId;
    }

    /**
     * Attempts to consume a token from the bucket for the specified key.
     * This method handles the core rate limiting logic including token refill and consumption.
     *
     * @param key The Redis key for the client's bucket
     * @return true if a token was successfully consumed, false otherwise
     */
    private boolean tryConsume(String key) {
        String tokensKey = key + ":" + TOKENS_KEY;
        String lastUpdateKey = key + ":" + LAST_UPDATE_KEY;

        // Start Redis transaction
        jedis.watch(tokensKey, lastUpdateKey);

        // Get current state
        String tokensStr = jedis.get(tokensKey);
        String lastUpdateStr = jedis.get(lastUpdateKey);

        // Initialize if not exists
        if (tokensStr == null || lastUpdateStr == null) {
            return initializeBucket(tokensKey, lastUpdateKey);
        }

        // Calculate refill
        long currentTime = Instant.now().toEpochMilli();
        long lastUpdate = Long.parseLong(lastUpdateStr);
        int currentTokens = Integer.parseInt(tokensStr);
        
        int refilledTokens = calculateRefilledTokens(currentTime, lastUpdate, currentTokens);
        
        // Try to consume token
        if (refilledTokens > 0) {
            return updateBucket(tokensKey, lastUpdateKey, refilledTokens - 1, currentTime);
        }

        return false;
    }

    /**
     * Initializes a new bucket for a client with the maximum capacity.
     *
     * @param tokensKey The Redis key for storing tokens
     * @param lastUpdateKey The Redis key for storing the last update timestamp
     * @return true if initialization was successful, false otherwise
     */
    private boolean initializeBucket(String tokensKey, String lastUpdateKey) {
        long currentTime = Instant.now().toEpochMilli();
        return updateBucket(tokensKey, lastUpdateKey, capacity - 1, currentTime);
    }

    /**
     * Calculates the number of tokens that should be in the bucket based on the time elapsed
     * since the last update.
     *
     * @param currentTime The current timestamp in milliseconds
     * @param lastUpdate The timestamp of the last update in milliseconds
     * @param currentTokens The current number of tokens in the bucket
     * @return The calculated number of tokens after refill
     */
    private int calculateRefilledTokens(long currentTime, long lastUpdate, int currentTokens) {
        long timePassed = currentTime - lastUpdate;
        int tokensToAdd = (int) (timePassed * refillRate / 1000);
        return Math.min(capacity, currentTokens + tokensToAdd);
    }

    /**
     * Updates the bucket state in Redis using a transaction to ensure atomicity.
     *
     * @param tokensKey The Redis key for storing tokens
     * @param lastUpdateKey The Redis key for storing the last update timestamp
     * @param newTokens The new number of tokens to store
     * @param currentTime The current timestamp in milliseconds
     * @return true if the update was successful, false otherwise
     */
    private boolean updateBucket(String tokensKey, String lastUpdateKey, int newTokens, long currentTime) {
        var transaction = jedis.multi();
        transaction.set(tokensKey, String.valueOf(newTokens));
        transaction.set(lastUpdateKey, String.valueOf(currentTime));
        List<Object> results = transaction.exec();
        return results != null && !results.isEmpty();
    }

    /**
     * Retrieves the current number of tokens in the bucket for a specific client.
     * This method is intended for testing purposes only.
     *
     * @param clientId The client identifier
     * @return The current number of tokens in the bucket
     */
    protected int getCurrentTokens(String clientId) {
        String key = getKey(clientId);
        String tokensStr = jedis.get(key + ":" + TOKENS_KEY);
        return tokensStr != null ? Integer.parseInt(tokensStr) : 0;
    }

    /**
     * Retrieves the timestamp of the last update for a specific client's bucket.
     * This method is intended for testing purposes only.
     *
     * @param clientId The client identifier
     * @return The timestamp of the last update in milliseconds
     */
    protected long getLastUpdateTime(String clientId) {
        String key = getKey(clientId);
        String lastUpdateStr = jedis.get(key + ":" + LAST_UPDATE_KEY);
        return lastUpdateStr != null ? Long.parseLong(lastUpdateStr) : 0;
    }
} 