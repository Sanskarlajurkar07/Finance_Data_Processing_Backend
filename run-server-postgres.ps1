$ErrorActionPreference = "Stop"

# Load environment variables from .env file
if (Test-Path ".env") {
    Write-Host "Loading environment variables from .env file..." -ForegroundColor Yellow
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
            Write-Host "  Set $name" -ForegroundColor Gray
        }
    }
    Write-Host ""
}

$JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
$MAVEN_WRAPPER_JAR = ".mvn\wrapper\maven-wrapper.jar"

Write-Host "Starting Spring Boot Application with PostgreSQL..." -ForegroundColor Green
Write-Host "Database: $env:DATABASE_URL" -ForegroundColor Cyan
Write-Host "Redis: $env:REDIS_HOST`:$env:REDIS_PORT" -ForegroundColor Cyan
Write-Host ""

# Check if wrapper jar exists, if not download it
if (-not (Test-Path $MAVEN_WRAPPER_JAR)) {
    Write-Host "Downloading Maven Wrapper..." -ForegroundColor Yellow
    $wrapperUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    Invoke-WebRequest -Uri $wrapperUrl -OutFile $MAVEN_WRAPPER_JAR
}

# Run Maven with Spring Boot
& "$JAVA_HOME\bin\java.exe" `
    -classpath $MAVEN_WRAPPER_JAR `
    "-Dmaven.multiModuleProjectDirectory=$PWD" `
    org.apache.maven.wrapper.MavenWrapperMain `
    spring-boot:run
