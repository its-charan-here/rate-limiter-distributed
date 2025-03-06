/**
 * Displays token history with area chart visualization
 */
import React from 'react';
import { 
  Paper, 
  Typography,
  IconButton,
  Tooltip,
  Box
} from '@mui/material';
import { Info as InfoIcon } from '@mui/icons-material';
import { 
  AreaChart, 
  Area, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip as RechartsTooltip,
  Legend,
  ResponsiveContainer 
} from 'recharts';
import { HistoryData } from '../types/api';

interface TokenHistoryChartProps {
  history: HistoryData[];
  capacity: number;
}

export const TokenHistoryChart: React.FC<TokenHistoryChartProps> = ({
  history,
  capacity
}) => {
  const formatTime = (timestamp: number) => {
    return new Date(timestamp).toLocaleTimeString();
  };

  return (
    <Paper sx={{ p: 3, height: '100%' }}>
      <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        Token History
        <Tooltip title="Visualizes token usage over time">
          <IconButton size="small">
            <InfoIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      </Typography>

      <Box sx={{ height: 300, mt: 2 }}>
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart
            data={history}
            margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
          >
            <defs>
              <linearGradient id="tokenGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#2196F3" stopOpacity={0.2}/>
                <stop offset="95%" stopColor="#2196F3" stopOpacity={0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#E0E0E0" />
            <XAxis 
              dataKey="time"
              type="number"
              domain={['auto', 'auto']}
              tickFormatter={formatTime}
              angle={-45}
              textAnchor="end"
              height={60}
              interval="preserveStartEnd"
            />
            <YAxis 
              domain={[0, capacity]}
              tickCount={6}
            />
            <RechartsTooltip 
              labelFormatter={formatTime}
              contentStyle={{ 
                backgroundColor: 'rgba(255, 255, 255, 0.95)',
                border: '1px solid #ccc',
                borderRadius: '8px',
                padding: '8px'
              }}
            />
            <Legend />
            <Line 
              type="monotone"
              dataKey="capacity"
              stroke="#FF9800"
              strokeWidth={2}
              dot={false}
              isAnimationActive={false}
              name="Capacity"
            />
            <Area
              type="monotone"
              dataKey="tokens"
              stroke="#2196F3"
              strokeWidth={2}
              fill="url(#tokenGradient)"
              isAnimationActive={false}
              name="Available Tokens"
            />
          </AreaChart>
        </ResponsiveContainer>
      </Box>
    </Paper>
  );
}; 