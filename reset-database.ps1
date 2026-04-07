$ErrorActionPreference = "Stop"

# Load environment variables from .env file
if (Test-Path ".env") {
    Write-Host "Loading environment variables from .env file..." -ForegroundColor Yellow
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

Write-Host "Resetting PostgreSQL database..." -ForegroundColor Green
Write-Host ""

# PostgreSQL connection details
$PGPASSWORD = $env:DATABASE_PASSWORD
$env:PGPASSWORD = $PGPASSWORD

# Drop and recreate database
Write-Host "Dropping existing database..." -ForegroundColor Yellow
psql -U $env:DATABASE_USERNAME -h localhost -c "DROP DATABASE IF EXISTS financedb;"

Write-Host "Creating fresh database..." -ForegroundColor Yellow
psql -U $env:DATABASE_USERNAME -h localhost -c "CREATE DATABASE financedb;"

Write-Host ""
Write-Host "Database reset complete!" -ForegroundColor Green
Write-Host "You can now start the server with: .\run-server-postgres.ps1" -ForegroundColor Cyan
