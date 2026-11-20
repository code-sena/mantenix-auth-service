# mantenix-auth-service

Mantenix access-control microservice: **users and roles**.

| Item | Value |
|------|-------|
| Port | `8081` |
| Database | PostgreSQL `mantenix_auth` (own database — ADR-004) |
| Stack | Java 21 · Spring Boot 4 · Maven · Flyway · springdoc-openapi (ADR-003) |
| Owner | Johann Tafur Farfán |
| Design docs | [code-sena/mantenix-docs](https://github.com/code-sena/mantenix-docs) |

## Run locally

Requirements: JDK 21 or newer and Docker.

```bash
docker compose up -d            # PostgreSQL 17 on localhost:5431
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

| What | URL |
|------|-----|
| Swagger UI | http://localhost:8081/swagger-ui.html |
| Health check | http://localhost:8081/actuator/health |

Unit tests (no database needed): `./mvnw test`

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/users` | Create a user (gets the `USER` role) |
| `GET` | `/users?role=` | List active users (optional filter by role) |
| `GET` | `/users/{id}` | Get a user |
| `PUT` | `/users/{id}` | Update email and name |
| `PUT` | `/users/{id}/roles` | Replace the roles of a user |
| `DELETE` | `/users/{id}` | Soft delete (`deleted_at`) |
| `GET` | `/roles` | List the roles: `ADMIN`, `USER`, `MECHANIC` |

## Business rules

| Rule | Error |
|------|-------|
| The email is unique and stored in lowercase | `409 EMAIL_ALREADY_EXISTS` |
| Password: 8–100 characters, one uppercase letter and one number | `400 VALIDATION_ERROR` |
| Passwords are stored only as a bcrypt hash (cost 12) and never returned | — |
| New users always get the `USER` role; roles change only with `PUT /users/{id}/roles` | — |

## Phase 1 scope

Login with JWT, refresh tokens, logout and `/jwks` (see `07-api/contracts/openapi/auth-service.yaml`)
are planned for the next phase.
