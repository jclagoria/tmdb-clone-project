# TMDB Clone API

REST API built with Spring Boot WebFlux to interact with The Movie Database (TMDB) API.

## Technologies

| Category | Technology |
|----------|------------|
| Framework | Spring Boot 3.5 (WebFlux) |
| Language | Java 21 |
| HTTP Client | WebClient (Reactive) |
| Resilience | Resilience4j (Circuit Breaker, Retry, TimeLimiter) |
| Documentation | SpringDoc OpenAPI (Swagger) |
| Environment | springboot3-dotenv |

## Architecture

```
┌─────────────────────────────────────┐
│         ADAPTER INBOUND              │
│   (REST Controllers)               │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         APPLICATION                 │
│   (Use Cases / DTOs)                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│            DOMAIN                   │
│   (Models, Ports, Services)        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         ADAPTER OUTBOUND            │
│   (TMDB WebClient)                  │
└─────────────────────────────────────┘
```

- **Domain**: Pure business logic, no external dependencies
- **Application**: Use cases orchestrating domain services
- **Adapter Inbound**: REST controllers
- **Adapter Outbound**: External TMDB API integration

## Configuration

Create `.env` file:
```env
TMDB_API_URL=https://api.themoviedb.org/3
TMDB_API_ACCESS_TOKEN=your_token_here
```

Or export variables:
```bash
export TMDB_API_URL="https://api.themoviedb.org/3"
export TMDB_API_ACCESS_TOKEN="your_token"
```

## Run

```bash
./mvnw spring-boot:run
```

## API Documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /actuator/health` | Overall health status |
| `GET /actuator/health/liveness` | Kubernetes liveness probe |
| `GET /actuator/health/readiness` | Kubernetes readiness probe |
| `GET /actuator/info` | Application information |
| `GET /actuator/metrics` | Application metrics |
| `GET /actuator/circuitbreakers` | Circuit breaker states |

## Resilience4j

Circuit breaker configured for TMDB API calls:
- Failure rate threshold: 30%
- Wait duration in open state: 5s
- Retry attempts: 3 (exponential backoff)
- Timeout: 10s

## Project Structure

```
src/main/java/com/api/tmdb/
├── adapter/
│   ├── inbound/          # REST Controllers
│   └── outbound/         # TMDB Client
├── application/          # Use Cases, DTOs
├── domain/               # Models, Ports, Services
└── config/               # Spring Configuration
```