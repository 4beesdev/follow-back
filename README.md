# Oris Backend API (`follow-api`)

## Overview
Spring Boot 2.1 REST API for the Oris GPS fleet management platform. Handles user authentication, vehicle management, driver management, geofencing, notifications, reporting, and integrates with the GPS data service (follow-gps-data) and GPS receiver servers.

## Tech Stack
- Java 8, Spring Boot 2.1.1
- Spring Security with JWT (HMAC512)
- PostgreSQL (via Spring Data JPA / Hibernate)
- HikariCP connection pool
- Firebase Cloud Messaging (push notifications)
- Maven build
- Docker multi-stage build

## Project Structure
```
src/main/java/rs/oris/back/
├── config/           # Configuration & security (JWT, CORS, WebSecurity)
├── controller/       # REST controllers (~46 endpoints)
├── domain/           # JPA entities, DTOs, projections
├── exceptions/       # Custom exceptions
├── export/           # PDF/XML export logic
├── repository/       # JPA repositories (48)
├── schedule/         # Scheduled background tasks
├── service/          # Business logic services (46)
├── util/             # Utility classes
└── web/              # REST communication helpers
```

## Key Components
- **Security**: JWT-based auth with access tokens (30min) and refresh tokens (7 days). Secret loaded from `JWT_SECRET` env var. CORS restricted to allowed origins via `CORS_ALLOWED_ORIGINS` env var.
- **Auth Flow**: Login via `POST /login` → returns access + refresh token. Refresh via `POST /api/auth/refresh`. All controllers use `AuthUtil.getCurrentUsername()` via Spring SecurityContext.
- **Health Check**: `GET /api/admin/health` runs parallel checks on PostgreSQL, follow-gps-data, MongoDB, GPS servers (GS100, Teltonika). Returns per-service response times.
- **Reporting**: Extensive report generation (PDF/XLS) for vehicle routes, fuel consumption, driver relations, mileage, sensor data.

## Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/test` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `4beesJadran` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `NekiRandomPWD` |
| `ORIS_MONGO_BASE_URL` | follow-gps-data service URL | `http://follow-gps-data:8080` |
| `JWT_SECRET` | JWT signing secret (REQUIRED in production) | auto-generated dev fallback |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins | `http://localhost:8888,...` |
| `GCP_FIREBASE_SERVICE_ACCOUNT` | Firebase credentials path | `classpath:firebase.json` |
| `SERVER_PORT` | HTTP port | `8000` |
| `GOTENBERG_URL` | Gotenberg PDF converter URL | `http://gotenberg:3000` |

## Running

### With Docker Compose (recommended)
```bash
docker compose -f docker-compose.dev.yml up --build follow-api
```

### Standalone
```bash
cp .env.example .env   # edit with your values
./mvnw spring-boot:run
```

## API Endpoints (key ones)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/login` | Authenticate user, get JWT tokens |
| POST | `/api/auth/refresh` | Refresh access token |
| GET | `/api/user/current` | Get current user info |
| GET | `/api/admin/health` | System health check (superadmin) |
| GET | `/api/firm/{id}/vehicle` | List vehicles for a firm |
| GET | `/api/user` | List users |
| POST | `/api/firm/{id}/user` | Create user |

## Dependencies
Communicates with:
- **PostgreSQL** — user, vehicle, firm, route data
- **follow-gps-data** — GPS data queries, MongoDB health
- **GPS Receiver Servers** — TCP health checks (ports 9876, 9877)
- **Firebase** — push notifications
- **Gotenberg** — PDF generation
