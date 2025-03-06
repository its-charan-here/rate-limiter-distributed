/**
 * Main application component for the Rate Limiter Demo
 * Displays real-time token usage and rate limiting statistics
 */
import React, { useState, useEffect } from 'react';
import { Container, Box, Typography, Grid } from '@mui/material';
import axios from 'axios';
import { StatusCard } from './components/StatusCard';
import { TokenHistoryChart } from './components/TokenHistoryChart';
import { RateLimiterResponse, HistoryData } from './types/api';

const App: React.FC = () => {
  const [stats, setStats] = useState<RateLimiterResponse | null>({
    allowed: true,
    currentTokens: 10,
    capacity: 10,
    refillRate: 2
  }); // Initialize with default values
  const [loading, setLoading] = useState<boolean>(false);
  const [history, setHistory] = useState<HistoryData[]>([]);

  /**
   * Updates the history data while maintaining a 5-minute window
   */
  const updateHistory = (data: Omit<HistoryData, 'time'>): void => {
    setHistory(prev => {
      const now = Date.now();
      const fiveMinutesAgo = now - 300000;
      const filteredHistory = prev.filter(point => point.time > fiveMinutesAgo);
      return [...filteredHistory, { ...data, time: now }];
    });
  };

  /**
   * Sends a request to the rate limiter API and updates the stats
   */
  const handleSendRequest = async (): Promise<void> => {
    setLoading(true);
    try {
      const response = await axios.get<RateLimiterResponse>(
        'http://localhost:8080/api/rate-limiter/check/test-client'
      );
      setStats(response.data);
      updateHistory({
        tokens: response.data.currentTokens,
        capacity: response.data.capacity,
        allowed: response.data.allowed
      });
    } catch (error) {
      console.error('Rate limiter request failed:', error);
    } finally {
      setLoading(false);
    }
  };

  // Auto-refresh tokens every second
  useEffect(() => {
    if (!stats) return;

    const interval = setInterval(() => {
      const newTokens = Math.min(stats.capacity, stats.currentTokens + stats.refillRate);
      setStats(prev => prev ? { ...prev, currentTokens: newTokens } : null);
      updateHistory({
        tokens: newTokens,
        capacity: stats.capacity,
        allowed: true
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [stats]);

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          Rate Limiter Demo
        </Typography>
        
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <StatusCard 
              stats={stats} 
              loading={loading} 
              onSendRequest={handleSendRequest} 
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <TokenHistoryChart 
              history={history} 
              capacity={stats?.capacity || 10} 
            />
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
};

export default App;