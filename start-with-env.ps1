# Start server with environment variables from .env file

Write-Host "Loading environment variables from .env file..." -ForegroundColor Yellow

# Read .env file and set environment variables
if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
            Write-Host "Set $name" -ForegroundColor Green
        }
    }
} else {
    Write-Host "ERROR: .env file not found!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Starting server..." -ForegroundColor Cyan
Write-Host ""

# Start Maven with Spring Boot
& .\mvnw-fixed.cmd spring-boot:run
