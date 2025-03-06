import { Socket as IOSocket } from 'socket.io-client';
import { RateLimiterResponse } from './api';

export interface ServerToClientEvents {
  rateLimiterUpdate: (data: RateLimiterResponse) => void;
}

export interface ClientToServerEvents {
  sendRequest: () => void;
  startRequests: (data: { tps: number }) => void;
  stopRequests: () => void;
  resetTokens: () => void;
}

export type AppSocket = IOSocket<ServerToClientEvents, ClientToServerEvents>;