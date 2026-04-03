@echo off
REM Finance Data Processing Backend - Development Startup Script

echo ==========================================
echo Finance Data Processing Backend
echo Development Environment Setup
echo ==========================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker is not running. Please start Docker first.
    exit /b 1
)

echo Docker is running
echo.

REM Start PostgreSQL and Redis containers
echo Starting PostgreSQL and Redis containers...
docker-compose up -d

echo.
echo Waiting for services to be ready...
timeout /t 5 /nobreak >nul

REM Check PostgreSQL health
echo Checking PostgreSQL...
:wait_postgres
docker exec finance-postgres pg_isready -U postgres >nul 2>&1
if errorlevel 1 (
    echo   Waiting for PostgreSQL...
    timeout /t 2 /nobreak >nul
    goto wait_postgres
)
echo PostgreSQL is ready

REM Check Redis health
echo Checking Redis...
:wait_redis
docker exec finance-redis redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo   Waiting for Redis...
    timeout /t 2 /nobreak >nul
    goto wait_redis
)
echo Redis is ready

echo.
echo ==========================================
echo Services are ready!
echo ==========================================
echo.
echo PostgreSQL: localhost:5432
echo   Database: financedb
echo   Username: postgres
echo   Password: postgres
echo.
echo Redis: localhost:6379
echo.
echo ==========================================
echo Starting Spring Boot application...
echo ==========================================
echo.

REM Start the Spring Boot application
mvnw.cmd spring-boot:run
