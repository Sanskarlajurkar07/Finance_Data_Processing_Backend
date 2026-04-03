# PostgreSQL Setup Script for Finance Backend

Write-Host "=== PostgreSQL Setup for Finance Backend ===" -ForegroundColor Cyan
Write-Host ""

# Check if PostgreSQL is installed
Write-Host "Checking PostgreSQL installation..." -ForegroundColor Yellow
$pgPath = Get-Command psql -ErrorAction SilentlyContinue

if (-not $pgPath) {
    Write-Host "ERROR: PostgreSQL is not found in PATH" -ForegroundColor Red
    Write-Host "Please ensure PostgreSQL is installed and added to PATH" -ForegroundColor Red
    Write-Host "You can download PostgreSQL from: https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
    exit 1
}

Write-Host "PostgreSQL found: $($pgPath.Source)" -ForegroundColor Green
Write-Host ""

# Get PostgreSQL credentials
Write-Host "Enter PostgreSQL credentials:" -ForegroundColor Yellow
$dbUser = Read-Host "Username (default: postgres)"
if ([string]::IsNullOrWhiteSpace($dbUser)) {
    $dbUser = "postgres"
}

$dbPassword = Read-Host "Password" -AsSecureString
$dbPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPassword)
)

# Create database
Write-Host ""
Write-Host "Creating database 'financedb'..." -ForegroundColor Yellow

$env:PGPASSWORD = $dbPasswordPlain
$createDbCommand = "CREATE DATABASE financedb;"
$result = & psql -U $dbUser -h localhost -c $createDbCommand 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "Database 'financedb' created successfully!" -ForegroundColor Green
} elseif ($result -like "*already exists*") {
    Write-Host "Database 'financedb' already exists. Skipping creation." -ForegroundColor Yellow
} else {
    Write-Host "ERROR: Failed to create database" -ForegroundColor Red
    Write-Host $result -ForegroundColor Red
    exit 1
}

# Create .env file
Write-Host ""
Write-Host "Creating .env file..." -ForegroundColor Yellow

$envContent = @"
# Database Configuration (PostgreSQL)
DATABASE_URL=jdbc:postgresql://localhost:5432/financedb
DATABASE_USERNAME=$dbUser
DATABASE_PASSWORD=$dbPasswordPlain

# Redis Configuration (Optional - for rate limiting)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT Configuration
JWT_SECRET=your-512-bit-secret-key-change-this-in-production-environment-must-be-at-least-512-bits
"@

$envContent | Out-File -FilePath ".env" -Encoding UTF8
Write-Host ".env file created successfully!" -ForegroundColor Green

Write-Host ""
Write-Host "=== Setup Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Run: .\run-server.ps1" -ForegroundColor White
Write-Host "2. Server will start on http://localhost:8080" -ForegroundColor White
Write-Host "3. Flyway will automatically create tables and insert default admin user" -ForegroundColor White
Write-Host ""
Write-Host "Default admin credentials:" -ForegroundColor Yellow
Write-Host "  Username: admin" -ForegroundColor White
Write-Host "  Password: admin123" -ForegroundColor White
Write-Host ""
