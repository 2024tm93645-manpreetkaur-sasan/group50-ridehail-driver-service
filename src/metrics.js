import client from 'prom-client';
import express from 'express';

const register = new client.Registry();
let httpHistogram;

export function initMetrics() {
  client.collectDefaultMetrics({ register });

  httpHistogram = new client.Histogram({
    name: 'http_request_duration_seconds',
    help: 'Duration of HTTP requests in seconds',
    labelNames: ['method', 'route', 'code'],
    buckets: [0.01, 0.05, 0.1, 0.2, 0.5, 1, 2, 5]
  });
  register.registerMetric(httpHistogram);
}

export function metricsMiddleware(req, res, next) {
  const end = httpHistogram.startTimer();
  res.on('finish', () => {
    const route = req.route && req.route.path ? req.route.path : req.path;
    end({ method: req.method, route, code: res.statusCode });
  });
  next();
}

export const metricsRouter = express.Router();
metricsRouter.get('/', async (req, res) => {
  res.set('Content-Type', register.contentType);
  res.end(await register.metrics());
});
