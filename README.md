# Finance Data Processing Backend

A secure Finance Data Processing and Access Control Backend system built with Java 17 and Spring Boot 3.x.

## Features

- **Secure Authentication**: JWT-based authentication with 15-minute token expiry
- **Role-Based Access Control**: Three-tier role system (ADMIN, ANALYST, VIEWER)
- **Financial Record Management**: CRUD operations with idempotency guarantees
- **Real-Time Analytics**: Dashboard with Redis caching (5-minute TTL)
- **Audit Logging**: Comprehensive AOP-based audit trail
- **Rate Limiting**: Protection against brute force attacks
- **High Precision**: BigDecimal arithmetic for financial calculations (NUMERIC 19,4)

## Technology Stack

- **Java**: 17
- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL 14+
- **Cache**: Redis 7+
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, jqwik (property-based testing), Testcontainers
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- Redis 7+

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd finance-data-processing-backend
```

### 2. Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE financedb;
```

### 3. Configure Environment Variables

Set the following environment variables or update `application.yml`:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/financedb
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your_password
export REDIS_HOST=localhost
export REDIS_PORT=6379
export JWT_SECRET=your-512-bit-secret-key
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run Database Migrations

Flyway migrations run automatically on application startup.

### 6. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### 7. Default Admin Credentials

- **Username**: `admin`
- **Password**: `Admin@123`

**⚠️ IMPORTANT**: Change the default admin password immediately in production!

## API Endpoints

### Authentication

- `POST /auth/login` - Authenticate and receive JWT token
- `POST /auth/refresh` - Refresh JWT token

### Financial Records

- `POST /api/financial-records` - Create financial record (ADMIN only)
- `GET /api/financial-records` - List financial records (All roles)
- `GET /api/financial-records/{id}` - Get specific record (All roles)
- `PUT /api/financial-records/{id}` - Update record (ADMIN only)
- `DELETE /api/financial-records/{id}` - Soft delete record (ADMIN only)

### Dashboard

- `GET /api/dashboard/analytics` - Get aggregated analytics (ADMIN, ANALYST)

### User Management

- `POST /api/users` - Create user (ADMIN only)
- `GET /api/users/{id}` - Get user details (ADMIN only)
- `PUT /api/users/{id}/deactivate` - Deactivate user (ADMIN only)

### Audit Logs

- `GET /api/audit-logs` - Retrieve audit logs (ADMIN only)

### Health Check

- `GET /actuator/health` - Application health status
- `GET /actuator/health/liveness` - Liveness probe
- `GET /actuator/health/readiness` - Readiness probe

## Configuration

### Application Properties

Key configuration properties in `application.yml`:

- **JWT Expiration**: 15 minutes (900000 ms)
- **BCrypt Strength**: 12
- **Cache TTL**: 5 minutes (300 seconds)
- **Rate Limit**: 5 attempts per 15 minutes
- **Database Pool**: 5-20 connections

### Logging

Structured JSON logging is configured with Logback:

- **Application logs**: `logs/application.json`
- **Audit logs**: `logs/audit.json`
- **Log retention**: 30 days (application), 90 days (audit)

## Testing

### Run All Tests

```bash
mvn test
```

### Run Property-Based Tests Only

```bash
mvn test -Dtest="*Properties"
```

### Run Integration Tests

```bash
mvn verify
```

Integration tests use Testcontainers to spin up PostgreSQL and Redis containers automatically.

## Database Migrations

Flyway manages database schema migrations:

- **Location**: `src/main/resources/db/migration`
- **Naming**: `V{version}__{description}.sql`
- **Execution**: Automatic on application startup

### Migration Files

1. `V1__create_users_table.sql` - Users table
2. `V2__create_financial_records_table.sql` - Financial records table
3. `V3__create_audit_logs_table.sql` - Audit logs table
4. `V4__insert_default_admin_user.sql` - Default admin user

## Security

### Password Hashing

- **Algorithm**: BCrypt
- **Strength**: 12 (2^12 iterations)
- **Storage**: Never store plaintext passwords

### JWT Tokens

- **Algorithm**: HS512 (HMAC with SHA-512)
- **Expiry**: 15 minutes
- **Secret**: 512-bit key (configure via environment variable)

### Rate Limiting

- **Max Attempts**: 5 failed logins
- **Window**: 15 minutes
- **Storage**: Redis-based state

## Architecture

### Layered Architecture

```
Controllers → Services → Repositories → Database
     ↓
  Security Filter (JWT)
     ↓
  Audit Aspect (AOP)
```

### Key Components

- **Authentication Service**: JWT token generation and validation
- **Rate Limiter Service**: Brute force protection
- **Financial Record Service**: CRUD with idempotency
- **Dashboard Service**: Analytics with caching
- **Audit Aspect**: AOP-based audit logging

## Monitoring

### Actuator Endpoints

- `/actuator/health` - Overall health
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information

### Health Checks

The application monitors:
- Database connectivity
- Redis connectivity
- Application status

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/fintech/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # Data access
│   │   ├── entity/          # JPA entities
│   │   ├── dto/             # Data transfer objects
│   │   ├── security/        # Security components
│   │   ├── aspect/          # AOP aspects
│   │   └── exception/       # Custom exceptions
│   └── resources/
│       ├── application.yml
│       ├── logback-spring.xml
│       └── db/migration/    # Flyway migrations
└── test/
    ├── java/com/fintech/
    │   ├── integration/     # Integration tests
    │   └── properties/      # Property-based tests
    └── resources/
        └── application-test.yml
```

## License

Copyright © 2024 FinTech Company. All rights reserved.
