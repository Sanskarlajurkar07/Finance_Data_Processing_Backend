# Manual Server Startup Guide

Since the Maven wrapper batch script has compatibility issues, use these manual commands:

## Method 1: Using PowerShell (Recommended)

Open PowerShell and run these commands:

```powershell
# Navigate to project directory
cd "E:\Zorvyn FinTech"

# Build the project
java -cp ".mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain clean install -DskipTests

# Or if that doesn't work, try:
java -jar .mvn\wrapper\maven-wrapper.jar clean install -DskipTests
```

## Method 2: Using Command Prompt (CMD)

Open Command Prompt and run:

```cmd
cd E:\Zorvyn FinTech
mvnw.cmd clean install -DskipTests
mvnw.cmd spring-boot:run
```

## Method 3: Direct Java Execution

If Maven wrapper fails, compile and run directly:

```powershell
# Compile
javac -d target/classes -cp "src/main/java" src/main/java/com/fintech/**/*.java

# Run (if JAR exists)
java -jar target/finance-data-processing-1.0.0.jar
```

## Method 4: Using IDE

1. Open project in IntelliJ IDEA or Eclipse
2. Right-click `FinanceDataProcessingApplication.java`
3. Select "Run"
4. Server starts automatically

## Expected Output

When server starts successfully:

```
Started FinanceDataProcessingApplication in X.XXX seconds
Tomcat started on port(s): 8080
```

## Verify Server

Open new terminal and run:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## If Build Fails

Try these troubleshooting steps:

```powershell
# Clear Maven cache
Remove-Item -Recurse -Force "$env:USERPROFILE\.m2\repository" -ErrorAction SilentlyContinue

# Clear project build
Remove-Item -Recurse -Force "target"

# Rebuild
java -jar .mvn\wrapper\maven-wrapper.jar clean install -DskipTests
```

## Alternative: Use Pre-built JAR

If you have a pre-built JAR file:

```powershell
java -jar target/finance-data-processing-1.0.0.jar
```

## Next Steps

1. Start server using one of the methods above
2. Verify with health check
3. Open Postman
4. Import `postman_collection.json`
5. Run API tests

---

**Note:** The Maven wrapper batch script has a syntax issue on Windows. Using PowerShell or IDE is recommended.
