import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { Box, useTheme } from '@mui/material';

interface TokenBucketGraphProps {
    data: Array<{
        time: string;
        tokens: number;
        requests: number;
    }>;
}

export const TokenBucketGraph: React.FC<TokenBucketGraphProps> = ({ data }) => {
    const theme = useTheme();

    return (
        <Box sx={{ width: '100%', height: 300 }}>
            <LineChart
                width={600}
                height={300}
                data={data}
                margin={{
                    top: 5,
                    right: 30,
                    left: 20,
                    bottom: 5,
                }}
            >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="time" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line
                    type="monotone"
                    dataKey="tokens"
                    stroke={theme.palette.primary.main}
                    name="Tokens"
                />
                <Line
                    type="monotone"
                    dataKey="requests"
                    stroke={theme.palette.secondary.main}
                    name="Requests"
                />
            </LineChart>
        </Box>
    );
};

export default TokenBucketGraph; 