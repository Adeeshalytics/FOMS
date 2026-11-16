# Financial Market Order Management System

FOMS is a single-instance, simulated exchange built as a Java portfolio project. It is for learning and demonstration only: it does not connect to real markets, accepts no real-money trades, and is **not production trading infrastructure**.

## Stage 1 status

The project foundation now provides a Java 21 / Spring Boot 3 application, PostgreSQL configuration, Flyway-managed initial schema, and pinned build dependencies. The schema establishes instruments, orders, executions, and audit events with core integrity constraints and indexes.

Subsequent stages will add the domain model, matching engine, REST API, idempotency, recovery, Docker support, and complete operational documentation.

## Prerequisites

- JDK 21
- Maven 3.9+ (or the included Maven wrapper once bootstrapped)
- PostgreSQL 16+

Set `FOMS_DB_URL`, `FOMS_DB_USERNAME`, and `FOMS_DB_PASSWORD` for a non-default local database. The defaults target a local `foms` database with the development-only `foms` username/password.

## Run

```bash
./mvnw spring-boot:run
```

Health will be exposed at `/actuator/health`; API documentation will be available at `/swagger-ui.html` once REST endpoints are introduced.

## Design constraints

- Financial values use `BigDecimal` in application code; no floating-point prices or monetary values.
- PostgreSQL is the planned durable source of truth.
- The intended matching design serializes processing per instrument and is not safe for multiple application instances.
