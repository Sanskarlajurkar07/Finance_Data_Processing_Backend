# Finance Backend Server Startup Script (No Docker Required)

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Finance Data Processing Backend" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1
    Write-Host "✓ Java found: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "✗ Error: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Java 17 or higher" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "[1/3] Building project..." -ForegroundColor Yellow
Write-Host ""

& .\mvnw.cmd clean install
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Build failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "[2/3] Build successful!" -ForegroundColor Green
Write-Host ""
Write-Host "[3/3] Starting server..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Server will start on http://localhost:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

& .\mvnw.cmd spring-boot:run

Read-Host "Press Enter to exit"
