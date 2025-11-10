import { AsyncLocalStorage } from 'node:async_hooks';
import { randomUUID } from 'node:crypto';

export const als = new AsyncLocalStorage();

export function correlationMiddleware(req, res, next) {
  const incoming = req.header('X-Correlation-Id');
  const correlationId = incoming && incoming.trim() !== '' ? incoming : randomUUID();
  als.run(new Map([['correlationId', correlationId]]), () => {
    res.setHeader('X-Correlation-Id', correlationId);
    next();
  });
}

export function getCorrelationId() {
  const store = als.getStore();
  return store ? store.get('correlationId') : undefined;
}
