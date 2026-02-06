# follow-api (jadran-server)

Oris server side Spring Boot API. Manages users, firms, vehicles, reports, notifications, geozones, and all administrative functionality. Communicates with PostgreSQL for persistent data and with follow-gps-data (galebmongo) for GPS/telemetry reports.

## Prerequisites

- Java 8 (JDK)
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional, for containerized runs)

## Configuration

Copy the example properties file and fill in your values:

```bash
cp src/main/resources/application.example.properties src/main/resources/application.properties
```

Or use environment variables (see `.env.example`).

## Run locally

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Run with Docker

```bash
docker build -t follow-api .
docker run --env-file .env -p 8000:8000 follow-api
```
