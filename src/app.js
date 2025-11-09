import express from 'express';
import dotenv from 'dotenv';
import { loggingMiddleware, logger } from './middleware/logging.js';
import { correlationMiddleware } from './middleware/correlation.js';
import { metricsMiddleware, metricsRouter, initMetrics } from './metrics.js';
import driversRouter from './routes/drivers.js';
import errorHandler from './middleware/error.js';

dotenv.config();

const app = express();

// Parse JSON
app.use(express.json());

// Correlation ID (first to ensure MDC is available for the rest)
app.use(correlationMiddleware);

// JSON logging
app.use(loggingMiddleware);

// Metrics
initMetrics();
app.use(metricsMiddleware);
app.use('/metrics', metricsRouter);

// Health
app.get('/health', (req, res) => res.json({ status: 'ok' }));

// Routes
app.use('/v1/drivers', driversRouter);

// Error handler (last)
app.use(errorHandler);

export default app;
