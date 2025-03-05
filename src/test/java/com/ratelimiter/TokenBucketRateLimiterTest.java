package com.ratelimiter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.text.MessageFormat;

@Testcontainers
class TokenBucketRateLimiterTest {
    private static final int REDIS_PORT = 6379;
    private static final String CLIENT_ID = "test-client";
    private static final int BUCKET_CAPACITY = 5;
    private static final int REFILL_RATE = 2;


    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(REDIS_PORT);

    private Jedis jedis;
    private TokenBucketRateLimiter rateLimiter;

    @BeforeEach
    public void setUp() {
        String redisHost = redis.getHost();
        Integer redisPort = redis.getMappedPort(REDIS_PORT);
        
        jedis = new Jedis(redisHost, redisPort);
        rateLimiter = new TokenBucketRateLimiter(jedis, BUCKET_CAPACITY, REFILL_RATE);
        
        // Clear Redis before each test
        jedis.flushAll();
    }

    @AfterEach
    public void tearDown() {
        if (jedis != null) {
            jedis.close();
        }
    }


    /**
     * Test scenario: Basic rate limiting functionality
     * Expected: Allow requests up to the bucket capacity, then deny
     */
    @Test
    void testTokenBucketRateLimiter() {
       for(int i = 0; i < BUCKET_CAPACITY; i++){
        assertTrue(rateLimiter.isAllowed(CLIENT_ID), MessageFormat.format("Request is expected to be allowed for the {0} time", i+1));
    }

    assertFalse(rateLimiter.isAllowed(CLIENT_ID), "Request exceeding capacity should be denied");

    }

    /**
     * Test scenario: Token refill after waiting
     * Expected: After waiting, tokens should be refilled
     * @throws InterruptedException 
     */
    @Test
    void tokenRefillAfterWaiting() throws InterruptedException {
          // Use all tokens
          for (int i = 0; i < BUCKET_CAPACITY; i++) {
            assertTrue(rateLimiter.isAllowed(CLIENT_ID), MessageFormat.format("Request is expected to be allowed for the {0} time", i+1));
        }

        Thread.sleep(REFILL_RATE * 2000);

        for (int i = 0; i < BUCKET_CAPACITY; i++) {
            assertTrue(rateLimiter.isAllowed(CLIENT_ID), MessageFormat.format("Since Refill Rate is {0} ,Request is expected to be allowed for the {1} time", REFILL_RATE, i+1));
        }

        // Request will be denied as the tokens have been used.
        assertFalse(rateLimiter.isAllowed(CLIENT_ID), "Request exceeding capacity should be denied");

    }


    @Test
    void multipleClients() {
        String clientId1 = "client1";
        String clientId2 = "client2";
        
        for (int i = 0; i < BUCKET_CAPACITY; i++) {
            assertTrue(rateLimiter.isAllowed(clientId1), MessageFormat.format("Request for client1 is expected to be allowed for the {0} time", i+1));
            assertTrue(rateLimiter.isAllowed(clientId2), MessageFormat.format("Request for client2 is expected to be allowed for the {0} time", i+1));
        }

        assertFalse(rateLimiter.isAllowed(clientId1), "Request exceeding capacity for client1 should be denied");
        assertFalse(rateLimiter.isAllowed(clientId2), "Request exceeding capacity for client2 should be denied");   

    }   

       /**
     * Test scenario: Partial refill
     * Expected: Tokens should be partially refilled based on elapsed time
     */
    @Test
    public void testPartialRefill() throws InterruptedException {
        // Use all tokens
        for (int i = 0; i < BUCKET_CAPACITY; i++) {
            assertTrue(rateLimiter.isAllowed(CLIENT_ID), MessageFormat.format("Request is expected to be allowed for the {0} time", i+1));
        }

        Thread.sleep(500); // refil rate is 2, so 500ms will add 1 token

        assertTrue(rateLimiter.isAllowed(CLIENT_ID), MessageFormat.format("Request is expected to be allowed for the {0} time", 1));

        // Request will be denied as the tokens have been used.
        assertFalse(rateLimiter.isAllowed(CLIENT_ID), "Request exceeding capacity should be denied");
        
    }
} 