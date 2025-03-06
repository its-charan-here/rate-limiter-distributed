import React from 'react';
import { Box, Paper, Typography, Slider, Stack } from '@mui/material';
import { motion } from 'framer-motion';

interface RequestPipelineProps {
  tps: number;
  onTpsChange: (value: number) => void;
  isRunning: boolean;
  currentTokens: number;
  capacity: number;
  refillRate: number;
}

const RequestPipeline: React.FC<RequestPipelineProps> = ({
  tps,
  onTpsChange,
  isRunning,
  currentTokens,
  capacity,
  refillRate,
}) => {
  return (
    <Paper sx={{ p: 3, mt: 4 }}>
      <Stack spacing={3}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography variant="h6">Request Rate (TPS)</Typography>
          <Slider
            value={tps}
            onChange={(_, value) => onTpsChange(value as number)}
            min={0}
            max={10}
            marks
            valueLabelDisplay="auto"
            sx={{ width: 200 }}
          />
          <Typography>{tps} TPS</Typography>
        </Box>

        <Box sx={{ position: 'relative', height: 200, display: 'flex', alignItems: 'center' }}>
          {/* User */}
          <Box sx={{ position: 'absolute', left: 0, textAlign: 'center' }}>
            <motion.div
              animate={isRunning ? { y: [0, -20, 0] } : {}}
              transition={{ duration: 1, repeat: Infinity, repeatType: "reverse" }}
            >
              👤
            </motion.div>
            <Typography>User</Typography>
          </Box>

          {/* Load Balancer */}
          <Box sx={{ position: 'absolute', left: '25%', textAlign: 'center' }}>
            <motion.div
              animate={isRunning ? { scale: [1, 1.2, 1] } : {}}
              transition={{ duration: 1, repeat: Infinity }}
            >
              ⚖️
            </motion.div>
            <Typography>Load Balancer</Typography>
          </Box>

          {/* Rate Limiter */}
          <Box sx={{ position: 'absolute', left: '50%', textAlign: 'center' }}>
            <motion.div
              animate={isRunning ? { rotate: [0, 360] } : {}}
              transition={{ duration: 2, repeat: Infinity, ease: "linear" }}
            >
              🚦
            </motion.div>
            <Typography>Rate Limiter</Typography>
            <Typography variant="caption" color="text.secondary">
              {currentTokens}/{capacity} tokens
            </Typography>
          </Box>

          {/* Backend */}
          <Box sx={{ position: 'absolute', left: '75%', textAlign: 'center' }}>
            <motion.div
              animate={isRunning ? { y: [0, 20, 0] } : {}}
              transition={{ duration: 1, repeat: Infinity, repeatType: "reverse" }}
            >
              🖥️
            </motion.div>
            <Typography>Backend</Typography>
          </Box>

          {/* Request Flow Lines */}
          <Box sx={{ position: 'absolute', top: '50%', left: 0, right: 0, height: 2, bgcolor: 'grey.300' }}>
            <motion.div
              animate={isRunning ? { x: ['0%', '100%'] } : {}}
              transition={{ duration: 2, repeat: Infinity, ease: "linear" }}
              style={{
                position: 'absolute',
                top: 0,
                left: 0,
                width: 20,
                height: 2,
                backgroundColor: '#007BFF',
              }}
            />
          </Box>
        </Box>
      </Stack>
    </Paper>
  );
};

export default RequestPipeline; 