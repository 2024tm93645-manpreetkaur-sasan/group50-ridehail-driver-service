import { logger } from './logging.js';
import { getCorrelationId } from './correlation.js';

export default function errorHandler(err, req, res, next) {
  const status = err.status || 500;
  const payload = {
    error: {
      message: err.message || 'Internal Server Error',
      code: err.code || 'INTERNAL_ERROR'
    },
    correlationId: getCorrelationId()
  };
  logger.error({ err, correlationId: payload.correlationId, status }, 'request_error');
  res.status(status).json(payload);
}
