package com.ratelimiter.controller;

import com.ratelimiter.model.RateLimiterRequest;
import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/rate-limiter")
@CrossOrigin(origins = "http://localhost:3000")
public class RateLimiterController {

    @Autowired
    private TokenBucketRateLimiter rateLimiter;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger currentTps = new AtomicInteger(0);

    @PostMapping("/start")
    public void startRequests(@RequestBody RateLimiterRequest request) {
        isRunning.set(true);
        currentTps.set(request.getTps());
    }

    @PostMapping("/stop")
    public void stopRequests() {
        isRunning.set(false);
        currentTps.set(0);
    }

    @Scheduled(fixedRate = 1000)
    public void processRequests() {
        if (!isRunning.get()) return;

        int tps = currentTps.get();
        for (int i = 0; i < tps; i++) {
            RateLimiterResponse response = rateLimiter.check("test-client");
            messagingTemplate.convertAndSend("/topic/rateLimiterUpdate", response);
        }
    }

    @GetMapping("/check/{clientId}")
    public RateLimiterResponse check(@PathVariable String clientId) {
        return rateLimiter.check(clientId);
    }
} 