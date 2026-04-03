@echo off
REM Finance Backend Server Startup Script (No Docker Required)

echo.
echo ========================================
echo Finance Data Processing Backend
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

echo [1/3] Building project...
echo.
call mvnw.cmd clean install
if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Build successful!
echo.
echo [3/3] Starting server...
echo.
echo Server will start on http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.

call mvnw.cmd spring-boot:run

pause
