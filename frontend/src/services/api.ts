import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export interface RateLimiterStats {
    allowed: boolean;
    currentTokens: number;
    capacity: number;
    refillRate: number;
    requestCount: number;
    allowedCount: number;
    successRate: number;
}

export const simulateRequest = async (clientId: string): Promise<RateLimiterStats> => {
    const response = await axios.post(`${API_URL}/api/rate-limiter/simulate`, { clientId });
    return response.data;
};

export const getStats = async (clientId: string): Promise<RateLimiterStats> => {
    const response = await axios.get(`${API_URL}/api/rate-limiter/stats/${clientId}`);
    return response.data;
}; 