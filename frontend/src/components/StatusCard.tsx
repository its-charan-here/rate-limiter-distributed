/**
 * Displays current rate limiter status and controls
 */
import React from 'react';
import { 
  Paper, 
  Typography, 
  Button, 
  Box, 
  CircularProgress,
  IconButton,
  Tooltip
} from '@mui/material';
import { 
  Refresh as RefreshIcon,
  Info as InfoIcon 
} from '@mui/icons-material';
import { RateLimiterResponse } from '../types/api';

interface StatusCardProps {
  stats: RateLimiterResponse | null;
  loading: boolean;
  onSendRequest: () => void;
}

export const StatusCard: React.FC<StatusCardProps> = ({ 
  stats, 
  loading, 
  onSendRequest 
}) => {
  return (
    <Paper sx={{ p: 3, height: '100%' }}>
      <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        Current Status
        <Tooltip title="Click the button to send a request and see the rate limiter in action">
          <IconButton size="small">
            <InfoIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      </Typography>
      
      <Box sx={{ mt: 2 }}>
        <Typography 
          sx={{ 
            color: stats?.allowed ? 'success.main' : 'error.main',
            fontWeight: 'bold',
            mb: 2
          }}
        >
          Status: {stats?.allowed ? 'Allowed' : 'Rate Limited'}
        </Typography>
        
        <Typography sx={{ mb: 1 }}>
          Current Tokens: {stats?.currentTokens}
        </Typography>
        <Typography sx={{ mb: 1 }}>
          Capacity: {stats?.capacity}
        </Typography>
        <Typography sx={{ mb: 2 }}>
          Refill Rate: {stats?.refillRate}/s
        </Typography>
        
        <Button 
          variant="contained" 
          onClick={onSendRequest}
          disabled={loading}
          startIcon={loading ? <CircularProgress size={20} /> : <RefreshIcon />}
          fullWidth
          sx={{
            mt: 2,
            bgcolor: 'primary.main',
            '&:hover': {
              bgcolor: 'primary.dark',
            }
          }}
        >
          {loading ? 'Sending...' : 'Send Request'}
        </Button>
      </Box>
    </Paper>
  );
}; 