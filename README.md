# group50-ridehail-driver-service
Manages driver profiles, vehicle information, and active/inactive status

# Driver Service API — README

## Overview
This document describes the driver & location-related HTTP APIs included in the Postman collection. Each endpoint entry contains what it does, HTTP method, URL pattern, expected headers, request body (if any), query parameters (if any), and example curl requests / payloads for quick testing.

> Note: The collection uses `localhost` with ports `8080` and `8081` (for local testing). Use the correct port for the endpoint you're calling.

---

## Common headers
- `Content-Type: application/json` — required for endpoints that accept JSON body payloads.

---

# Endpoints

### 1. Create Driver
- **Method:** `POST`
- **Path:** `/api/v1/drivers`
- **Sample URL:** `http://localhost:8080/api/v1/drivers`
- **Description:** Create a new driver record.
- **Headers:** `Content-Type: application/json`
- **Request body (JSON):**
```json
{
  "name": "John Doe",
  "phone": "9999999999",
  "email": "john@example.com",
  "licenseNumber": "MH12-AB-1234"
}
```
- **Example curl:**
```bash
curl -X POST "http://localhost:8080/api/v1/drivers"   -H "Content-Type: application/json"   -d '{"name":"John Doe","phone":"9999999999","email":"john@example.com","licenseNumber":"MH12-AB-1234"}'
```

---

### 2. List Drivers
- **Method:** `GET`
- **Path:** `/api/v1/drivers`
- **Sample URL:** `http://localhost:8081/api/v1/drivers`
- **Description:** Retrieve a list of all drivers.
- **Example curl:**
```bash
curl "http://localhost:8081/api/v1/drivers"
```

---

### 3. Get Driver by ID
- **Method:** `GET`
- **Path:** `/api/v1/drivers/{driverId}`
- **Sample URL:** `http://localhost:8081/api/v1/drivers/1`
- **Description:** Fetch details for a single driver by id.
- **Example curl:**
```bash
curl "http://localhost:8081/api/v1/drivers/1"
```

---

### 4. Update Driver (PUT)
- **Method:** `PUT`
- **Path:** `/api/v1/drivers/{driverId}`
- **Sample URL:** `http://localhost:8081/api/v1/drivers/1`
- **Description:** Replace/update driver information (full update).
- **Headers:** `Content-Type: application/json`
- **Request body (JSON example):**
```json
{
  "name": "John Updated",
  "phone": "9999999999",
  "email": "john.updated@example.com",
  "licenseNumber": "MH12-AB-9999",
  "isActive": true
}
```

---

### 5. Delete Driver
- **Method:** `DELETE`
- **Path:** `/api/v1/drivers/{driverId}`
- **Sample URL:** `http://localhost:8081/api/v1/drivers/1`
- **Description:** Delete a driver by id.
- **Example curl:**
```bash
curl -X DELETE "http://localhost:8081/api/v1/drivers/1"
```

---

### 6. Set Driver Status (PATCH)
- **Method:** `PATCH`
- **Path:** `/api/v1/drivers/{driverId}/status`
- **Sample URL:** `http://localhost:8081/api/v1/drivers/2/status?active=true`
- **Description:** Toggle or set driver active/inactive status.
- **Query params:**
    - `active` — `true` or `false`

---

### 7. Add Vehicle (for a driver)
- **Method:** `POST`
- **Path:** `/api/v1/drivers/{driverId}/vehicles`
- **Sample URL:** `http://localhost:8081/api/v1/drivers/1/vehicles`
- **Description:** Add a vehicle record belonging to a driver.
- **Request body (JSON):**
```json
{
  "plate": "KA01AB0001",
  "make": "Toyota",
  "model": "Etios",
  "year": 2019
}
```

---

### 8. Get Vehicles for Driver
- **Method:** `GET`
- **Path:** `/api/v1/drivers/{driverId}/vehicles`
- **Sample URL:** `http://localhost:8081/api/v1/drivers/1/vehicles`
- **Description:** Retrieve all vehicles associated with a driver.

---

### 9. Update Location (PATCH)
- **Method:** `PATCH`
- **Path:** `/api/v1/drivers/{driverId}/location`
- **Sample URL:** `http://localhost:8081/api/v1/drivers/1/location`
- **Description:** Update the current GPS location and motion data for a driver.
- **Request body (JSON):**
```json
{
  "lat": 12.9716,
  "lon": 77.5946,
  "speed": 10.5,
  "heading": 180
}
```

---

### 10. Find Nearby Drivers
- **Method:** `GET`
- **Path:** `/api/v1/drivers/nearby`
- **Sample URL:** `http://localhost:8081/api/v1/drivers/nearby?lat=12.9716&lon=77.5946&radiusKm=3&limit=10`
- **Description:** Query drivers near a given coordinate within a radius.
- **Query params:**
    - `lat` — latitude
    - `lon` — longitude
    - `radiusKm` — radius in kilometers
    - `limit` — maximum number of results

---

## Status codes
- `200 OK`
- `201 Created`
- `204 No Content`
- `400 Bad Request`
- `404 Not Found`
- `500 Internal Server Error`

---

## Quick testing checklist
1. Ensure the service is running on `localhost:8080` or `localhost:8081`.
2. Use `Content-Type: application/json` for POST, PUT, PATCH requests.
3. Adjust query parameters according to your needs.
