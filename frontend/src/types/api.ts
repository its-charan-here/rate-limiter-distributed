export interface RateLimiterRequest {
    clientId: string;
    tps: number;
    capacity: number;
    refillRate: number;
}

/**
 * Response from the rate limiter API
 */
export interface RateLimiterResponse {
    allowed: boolean;
    currentTokens: number;
    capacity: number;
    refillRate: number;
}

/**
 * Data structure for token history visualization
 */
export interface HistoryData {
    time: number;
    tokens: number;
    capacity: number;
    allowed: boolean;
} 