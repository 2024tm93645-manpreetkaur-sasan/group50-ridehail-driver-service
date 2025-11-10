Here is the **Driver Service README with all icons/emojis removed**.
This version is clean, professional, and ready for production documentation.

---

# Driver Service (Node.js + Express)

The Driver Service manages driver profiles for the ride-hailing platform.
It exposes a REST API, stores data in PostgreSQL, and logs actions using structured JSON logs with correlation IDs.

**Tech Stack:**
Node.js 20+ • Express.js • PostgreSQL • Docker • Docker Compose
JSON Logging • Correlation ID Middleware
Postman / curl for testing

---

# Quickstart — Run with Docker

```bash
# 0) Clean previous stack (optional)
docker compose down -v
```

```bash
# 1) Build Driver Service image
cd driver-service
docker build --no-cache -t rhf/driver-service:latest .
cd ..
```

```bash
# 2) Start Driver Service + PostgreSQL
docker compose up -d
```

```bash
# 3) Check health
curl http://localhost:9084/health
```

```bash
# 4) List drivers
curl http://localhost:9084/v1/drivers
```

---

# Exposed Ports

| Component      | Port | Description                |
| -------------- | ---- | -------------------------- |
| Driver Service | 9084 | Express REST API           |
| PostgreSQL     | 5545 | Container 5432 → Host 5545 |

---

# API Endpoints

## Health

```
GET /health
GET /info
```

## Driver CRUD + Partial Update

```
GET     /v1/drivers
GET     /v1/drivers/:id
POST    /v1/drivers
PUT     /v1/drivers/:id
PATCH   /v1/drivers/:id
DELETE  /v1/drivers/:id
```

All responses return JSON.

---

# Sample Requests

## Create Driver

```bash
curl -X POST http://localhost:9084/v1/drivers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sarah Connor",
    "email": "sarah@driver.com",
    "phone": "555-2020",
    "vehicleType": "Sedan",
    "licenseNumber": "XYZ-9999"
  }'
```

## Partial Update (PATCH)

```bash
curl -X PATCH http://localhost:9084/v1/drivers/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleType": "SUV",
    "phone": "555-9000"
  }'
```

## Full Update (PUT)

```bash
curl -X PUT http://localhost:9084/v1/drivers/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Driver",
    "email": "new@driver.com",
    "phone": "555-1010",
    "vehicleType": "SUV"
  }'
```

## Get All Drivers

```bash
curl http://localhost:9084/v1/drivers
```

## Delete Driver

```bash
curl -X DELETE http://localhost:9084/v1/drivers/{id}
```

---

# JSON Logging and Correlation ID

The service includes middleware that ensures every request has a correlation ID.

### Behavior

* Reads the header `X-Correlation-Id`
* Generates a UUID if missing
* Adds correlation ID to all logs
* Response header includes the same ID

### Example Log Entry

```json
{
  "timestamp": "2025-02-10T15:12:32Z",
  "level": "info",
  "correlationId": "27f0dc12-595b-4d27-8d92-29d1221f1b04",
  "method": "GET",
  "path": "/v1/drivers",
  "status": 200,
  "durationMs": 11
}
```

---

# Database

### Seed Data

Loaded automatically at first startup:

```
data/rhfd_drivers.csv
```

### Schema

Created via:

```
driver-service/seed/schema.sql
```

---

# Docker Compose Commands

```bash
docker compose up -d                    # start
docker compose down -v                  # stop and remove volumes
docker compose logs -f driver-service   # follow logs
docker exec -it driver-db psql -U driver -d driverdb   # open PostgreSQL shell
```

---

