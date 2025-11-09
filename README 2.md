# Driver Service (Node.js + Express + Postgres)

Features:
- JSON logs (pino) with X-Correlation-Id (auto-generated if missing) stored via AsyncLocalStorage MDC
- Prometheus metrics at `/metrics` (default metrics + http_request_duration_seconds histogram)
- REST endpoints under `/v1/drivers`
- CSV seed imported into Postgres at container init
- Docker + docker-compose for local dev

## Quickstart

```bash
cp .env.example .env  # optional; compose uses defaults
docker compose up -d --build
```

### Health
- GET http://localhost:3000/health

### Metrics
- GET http://localhost:3000/metrics

### Endpoints
- GET    /v1/drivers
- GET    /v1/drivers/search?email=&name=&phone=
- GET    /v1/drivers/:id
- POST   /v1/drivers
- PUT    /v1/drivers/:id
- DELETE /v1/drivers/:id
- PATCH  /v1/drivers/:id/status   (body: { "is_active": true|false })

### Example: create
```bash
curl -H "Content-Type: application/json" -H "X-Correlation-Id: demo-123"       -d '{"name":"Dana","email":"dana@example.com","phone":"9995550000","vehicle_make":"Tata","vehicle_model":"Nexon","plate_number":"KA04GH4321"}'       http://localhost:3000/v1/drivers
```
