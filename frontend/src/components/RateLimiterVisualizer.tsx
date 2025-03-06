import React, { useState, useEffect } from 'react';
import { Box, Card, CardContent, Typography, Button, CircularProgress } from '@mui/material';
import TokenBucketGraph from './TokenBucketGraph';
import { simulateRequest, getStats } from '../services/api';
import { RateLimiterStats, DataPoint } from '../types';

export const RateLimiterVisualizer: React.FC = () => {
    const [stats, setStats] = useState<RateLimiterStats | null>(null);
    const [history, setHistory] = useState<DataPoint[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const clientId = 'default-client'; // You can make this configurable

    const updateStats = async () => {
        try {
            const newStats = await getStats(clientId);
            setStats(newStats);
            setHistory(prev => [...prev, {
                time: new Date().toLocaleTimeString(),
                tokens: newStats.currentTokens,
                requests: newStats.requestCount
            }].slice(-20)); // Keep last 20 points
        } catch (err) {
            setError('Failed to fetch stats');
        }
    };

    useEffect(() => {
        const interval = setInterval(updateStats, 1000);
        return () => clearInterval(interval);
    }, []);

    const handleSimulate = async () => {
        setLoading(true);
        try {
            await simulateRequest(clientId);
            await updateStats();
        } catch (err) {
            setError('Failed to simulate request');
        } finally {
            setLoading(false);
        }
    };

    if (error) {
        return (
            <Card>
                <CardContent>
                    <Typography color="error">{error}</Typography>
                </CardContent>
            </Card>
        );
    }

    return (
        <Card>
            <CardContent>
                <Typography variant="h5" gutterBottom>
                    Token Bucket Rate Limiter
                </Typography>
                {stats && (
                    <Box sx={{ mb: 2 }}>
                        <Typography>
                            Current Tokens: {stats.currentTokens}/{stats.capacity}
                        </Typography>
                        <Typography>
                            Refill Rate: {stats.refillRate} tokens/second
                        </Typography>
                        <Typography>
                            Success Rate: {stats.successRate.toFixed(2)}%
                        </Typography>
                    </Box>
                )}
                <Button
                    variant="contained"
                    onClick={handleSimulate}
                    disabled={loading}
                    sx={{ mb: 2 }}
                >
                    {loading ? <CircularProgress size={24} /> : 'Simulate Request'}
                </Button>
                <TokenBucketGraph data={history} />
            </CardContent>
        </Card>
    );
};

export default RateLimiterVisualizer; 