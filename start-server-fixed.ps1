Write-Host "Starting Finance Backend Server..." -ForegroundColor Cyan
Write-Host ""

Set-Location "E:\Zorvyn FinTech"

$env:MAVEN_OPTS = "-Xmx512m"

& cmd.exe /c mvnw-fixed.cmd spring-boot:run
