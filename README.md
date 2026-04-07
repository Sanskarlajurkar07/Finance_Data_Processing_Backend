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

**Option 1: Using PowerShell Script (Recommended for Windows)**

```powershell
.\run-server-postgres.ps1
```

**Option 2: Using Maven Wrapper (if mvnw works)**

```bash
mvn spring-boot:run
```

**Option 3: Clean Restart (drops and recreates database)**

```powershell
.\clean-restart.ps1
```

The application will start on `http://localhost:8080/api`.

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

## Architecture Decisions

### Why CP over CA (CAP Theorem)

Financial data correctness is non-negotiable. This system prioritizes **Consistency** and **Partition Tolerance** over Availability.

**Trade-off**: If Redis is unavailable, we fall back to database queries — slightly slower but never wrong data. For financial systems, showing stale or incorrect balances is worse than a 200ms delay.

### Why NUMERIC(19,4) not Double

```java
// Double precision error
0.1 + 0.2 = 0.30000000000000004  // ❌ Wrong

// BigDecimal precision
0.1 + 0.2 = 0.3000  // ✅ Correct
```

In finance, this difference is real money. NUMERIC(19,4) supports amounts up to 999 trillion with 4 decimal places, preventing floating-point rounding errors that compound over thousands of transactions.

### Why AOP for Audit Logging

Zero manual audit calls in service code. Every write operation is automatically captured via `@Aspect` and `@Auditable` annotation.

**Benefits**:
- Impossible to forget audit logging
- Consistent audit format across all operations
- Separation of concerns (business logic vs. compliance)
- This is how compliance-grade systems work in production

**Example**:
```java
@Auditable(actionType = ActionType.CREATE)
public FinancialRecord createRecord(CreateRecordRequest request) {
    // Business logic only — audit happens automatically
}
```

### Why Idempotency Keys

Network retries can cause duplicate financial transactions. Same idempotency key = same response, no duplicate record created.

**Real-world scenario**:
```
Client → [Network timeout] → Retry → Duplicate transaction ❌
Client → [Idempotency key] → Retry → Same record returned ✅
```

This is the Stripe/Razorpay pattern. Critical for:
- Mobile apps with unreliable networks
- Microservice architectures with retry logic
- Payment processing integrations

### Cache Stampede Tradeoff

`@CacheEvict` clears dashboard cache on every financial record write. If 100 analysts hit the dashboard simultaneously after cache eviction, 100 database queries fire.

**Why this is acceptable**:
- Dashboard queries are read-optimized with indexes
- Financial writes are infrequent compared to reads
- Alternative (stale cache) is worse for financial data
- At scale, implement cache warming or pub/sub invalidation

**Documented limitation**: For >1000 concurrent users, implement Redis pub/sub for selective cache invalidation.

### Why 15-Minute JWT Expiration

**Security vs. UX balance**:
- Shorter expiry (5 min) = more secure but annoying re-logins
- Longer expiry (1 hour) = better UX but higher risk if token stolen
- 15 minutes = industry standard compromise

**Mitigation**: Refresh tokens implemented for seamless re-authentication without password re-entry.

### Why Soft Delete over Hard Delete

Financial records are marked `isActive = false` instead of `DELETE FROM`.

**Compliance benefits**:
- Complete audit trail for regulatory requirements
- Data recovery if deletion was accidental
- Referential integrity preserved
- Supports GDPR "right to be forgotten" with anonymization

**Trade-off**: Requires `WHERE isActive = true` in all queries, but JPA specifications handle this transparently.

### Why BCrypt Strength 12

BCrypt with strength 12 (2^12 = 4,096 iterations) provides strong password protection while maintaining acceptable authentication performance (~200-300ms).

**Why not higher**:
- Strength 13-14 would be more secure but impacts UX
- Strength 12 is OWASP recommended for 2024
- Adjustable via configuration for future hardware improvements

### Why Redis for Rate Limiting

Distributed rate limiting using Redis enables:
- Horizontal scaling (multiple app instances share state)
- Automatic key expiration (15-minute windows)
- Sub-millisecond lookups

**Alternative considered**: Database-based rate limiting would work but adds 50-100ms latency per auth attempt.

## API Documentation

### Swagger/OpenAPI

Interactive API documentation available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Postman Collection

Import `postman_collection.json` for:
- Pre-configured requests for all endpoints
- Automated test assertions
- Environment variable management

## Known Limitations

### Current Scope

1. **Single Redis Node**: No clustering implemented (acceptable for assignment scope)
2. **No Distributed Transactions**: Across multiple services (not needed for monolith)
3. **JWT Revocation Not Implemented**: Stateless design tradeoff (tokens valid until expiry)
4. **Cache Stampede**: Possible under extreme load (documented above)
5. **No API Versioning**: `/api/v1/` not implemented (would add for production)

### Production Enhancements

For production deployment, consider:
- Redis Sentinel/Cluster for high availability
- Circuit breakers (Resilience4j) for external service calls
- Distributed tracing (Zipkin/Jaeger)
- Metrics aggregation (Prometheus + Grafana)
- API rate limiting per user (not just auth attempts)
- Database read replicas for analytics queries

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
