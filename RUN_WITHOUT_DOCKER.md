# Running Finance Backend WITHOUT Docker

## Prerequisites

- Java 17+ (already installed on your system)
- Maven (via mvnw.cmd - included in project)

## Step 1: Open Terminal in VS Code

Press **Ctrl + `** (backtick) to open the integrated terminal.

## Step 2: Build the Project

Run this command:
```bash
.\mvnw.cmd clean install
```

This will:
- Download dependencies
- Compile the code
- Run tests
- Create the JAR file

**Wait time:** 3-5 minutes (first time only)

## Step 3: Run the Application

After build completes, run:
```bash
.\mvnw.cmd spring-boot:run
```

Or directly with Java:
```bash
java -jar target/finance-data-processing-1.0.0.jar
```

## Step 4: Verify Server Started

You should see output like:
```
Started FinanceDataProcessingApplication in 8.234 seconds
Tomcat started on port(s): 8080
```

## Step 5: Test the Server

Open a new terminal tab and run:
```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "database": {"status": "UP"},
    "redis": {"status": "DOWN"}
  }
}
```

**Note:** Redis will show DOWN (that's OK - we're using embedded H2 database instead)

## Step 6: Use Postman to Test API

1. Open Postman
2. Import `postman_collection.json`
3. Select **Finance Local** environment
4. Run tests

## What's Different Without Docker?

| Feature | With Docker | Without Docker |
|---------|------------|-----------------|
| Database | PostgreSQL | H2 (Embedded) |
| Redis | Redis Server | Embedded (in-memory) |
| Data Persistence | Persistent | Lost on restart |
| Setup Time | 5-10 minutes | 1-2 minutes |
| Performance | Production-ready | Development-ready |

## Database Access

### H2 Console (Web UI)

While server is running, open browser:
```
http://localhost:8080/h2-console
```

Login with:
- JDBC URL: `jdbc:h2:mem:financedb`
- User: `sa`
- Password: (leave empty)

## Troubleshooting

### Build Fails
```bash
# Clear cache and rebuild
.\mvnw.cmd clean
.\mvnw.cmd install
```

### Port 8080 Already in Use
```bash
# Kill process using port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Out of Memory
```bash
# Increase heap size
set MAVEN_OPTS=-Xmx1024m
.\mvnw.cmd spring-boot:run
```

### Tests Fail During Build
```bash
# Skip tests and build
.\mvnw.cmd clean install -DskipTests
```

## Quick Commands Reference

| Command | Purpose |
|---------|---------|
| `.\mvnw.cmd clean install` | Build project |
| `.\mvnw.cmd spring-boot:run` | Run application |
| `.\mvnw.cmd test` | Run tests only |
| `.\mvnw.cmd clean` | Clean build artifacts |
| `curl http://localhost:8080/actuator/health` | Check health |

## Next Steps

1. ✓ Build project: `.\mvnw.cmd clean install`
2. ✓ Run server: `.\mvnw.cmd spring-boot:run`
3. ✓ Test health: `curl http://localhost:8080/actuator/health`
4. ✓ Open Postman and import collection
5. ✓ Run API tests

## Notes

- Data is stored in memory (H2 database)
- Data is lost when server restarts
- Perfect for development and testing
- For production, use PostgreSQL + Redis with Docker

## Support

If you encounter issues:
1. Check Java version: `java -version`
2. Check Maven: `.\mvnw.cmd --version`
3. Review build output for errors
4. Check port 8080 is available
