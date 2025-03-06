export interface RateLimiterStats {
    allowed: boolean;
    currentTokens: number;
    capacity: number;
    refillRate: number;
    requestCount: number;
    allowedCount: number;
    successRate: number;
}

export interface DataPoint {
    time: string;
    tokens: number;
    requests: number;
} 