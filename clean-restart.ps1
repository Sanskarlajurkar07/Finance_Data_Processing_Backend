$ErrorActionPreference = "Stop"

# Load environment variables from .env file
if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

Write-Host "Cleaning and restarting with fresh database..." -ForegroundColor Green
Write-Host ""

# Find PostgreSQL bin directory
$pgPaths = @(
    "C:\Program Files\PostgreSQL\14\bin",
    "C:\Program Files\PostgreSQL\15\bin",
    "C:\Program Files\PostgreSQL\16\bin",
    "C:\PostgreSQL\14\bin",
    "C:\PostgreSQL\15\bin",
    "C:\PostgreSQL\16\bin"
)

$psqlPath = $null
foreach ($path in $pgPaths) {
    if (Test-Path "$path\psql.exe") {
        $psqlPath = "$path\psql.exe"
        break
    }
}

if ($psqlPath) {
    Write-Host "Found PostgreSQL at: $psqlPath" -ForegroundColor Yellow
    $env:PGPASSWORD = $env:DATABASE_PASSWORD
    
    Write-Host "Dropping database..." -ForegroundColor Yellow
    & $psqlPath -U $env:DATABASE_USERNAME -h localhost -c "DROP DATABASE IF EXISTS financedb;" 2>&1 | Out-Null
    
    Write-Host "Creating fresh database..." -ForegroundColor Yellow
    & $psqlPath -U $env:DATABASE_USERNAME -h localhost -c "CREATE DATABASE financedb;" 2>&1 | Out-Null
    
    Write-Host "Database reset complete!" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host "PostgreSQL psql not found. Skipping database reset." -ForegroundColor Yellow
    Write-Host "The server will use the existing database." -ForegroundColor Yellow
    Write-Host ""
}

# Start the server
$JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
$MAVEN_WRAPPER_JAR = ".mvn\wrapper\maven-wrapper.jar"

Write-Host "Starting Spring Boot Application..." -ForegroundColor Green
Write-Host ""

& "$JAVA_HOME\bin\java.exe" `
    -classpath $MAVEN_WRAPPER_JAR `
    "-Dmaven.multiModuleProjectDirectory=$PWD" `
    org.apache.maven.wrapper.MavenWrapperMain `
    spring-boot:run
