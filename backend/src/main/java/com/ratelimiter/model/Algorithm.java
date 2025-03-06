package com.ratelimiter.model;

/**
 * Enum representing different rate limiting algorithms.
 * Currently only TOKEN_BUCKET is implemented.
 * Other algorithms (LEAKY_BUCKET, SLIDING_WINDOW) are placeholders for future implementation.
 */
public enum Algorithm {
    TOKEN_BUCKET,
    LEAKY_BUCKET,  // Not implemented yet
    SLIDING_WINDOW // Not implemented yet
} 