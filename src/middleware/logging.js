import pino from 'pino';
import pinoHttp from 'pino-http';
import { getCorrelationId } from './correlation.js';

export const logger = pino({
  level: process.env.LOG_LEVEL || 'info',
  base: null, // do not include pid/hostname by default
  messageKey: 'message',
  formatters: {
    level(label) { return { level: label }; },
    bindings(bindings) { return {}; }
  },
  timestamp: pino.stdTimeFunctions.isoTime // ISO timestamp
});

export const loggingMiddleware = pinoHttp({
  logger,
  customProps: (req, res) => ({
    correlationId: getCorrelationId(),
  }),
  customLogLevel: function (req, res, err) {
    if (res.statusCode >= 500 || err) return 'error';
    if (res.statusCode >= 400) return 'warn';
    return 'info';
  },
  serializers: {
    req(req) {
      return {
        method: req.method,
        url: req.url,
        headers: { 'x-correlation-id': req.headers['x-correlation-id'] },
      };
    },
    res(res) {
      return {
        statusCode: res.statusCode,
      };
    }
  }
});
