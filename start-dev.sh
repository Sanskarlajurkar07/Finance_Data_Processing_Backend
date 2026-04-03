#!/bin/bash

# Finance Data Processing Backend - Development Startup Script

echo "=========================================="
echo "Finance Data Processing Backend"
echo "Development Environment Setup"
echo "=========================================="
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running. Please start Docker first."
    exit 1
fi

echo "✓ Docker is running"
echo ""

# Start PostgreSQL and Redis containers
echo "Starting PostgreSQL and Redis containers..."
docker-compose up -d

echo ""
echo "Waiting for services to be ready..."
sleep 5

# Check PostgreSQL health
echo "Checking PostgreSQL..."
until docker exec finance-postgres pg_isready -U postgres > /dev/null 2>&1; do
    echo "  Waiting for PostgreSQL..."
    sleep 2
done
echo "✓ PostgreSQL is ready"

# Check Redis health
echo "Checking Redis..."
until docker exec finance-redis redis-cli ping > /dev/null 2>&1; do
    echo "  Waiting for Redis..."
    sleep 2
done
echo "✓ Redis is ready"

echo ""
echo "=========================================="
echo "Services are ready!"
echo "=========================================="
echo ""
echo "PostgreSQL: localhost:5432"
echo "  Database: financedb"
echo "  Username: postgres"
echo "  Password: postgres"
echo ""
echo "Redis: localhost:6379"
echo ""
echo "=========================================="
echo "Starting Spring Boot application..."
echo "=========================================="
echo ""

# Start the Spring Boot application
./mvnw spring-boot:run

# Cleanup on exit
trap "docker-compose down" EXIT
