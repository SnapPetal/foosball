#!/bin/bash

echo "🚀 Starting Foosball Backend Service..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Start PostgreSQL database
echo "🐘 Starting PostgreSQL database..."
docker-compose up -d postgres

# Wait for database to be ready
echo "⏳ Waiting for database to be ready..."
until docker-compose exec -T postgres pg_isready -U foosball_user -d foosball > /dev/null 2>&1; do
    echo "   Still waiting..."
    sleep 2
done

echo "✅ Database is ready!"

# Start Spring Boot application
echo "☕ Starting Spring Boot application..."
mvn spring-boot:run

echo "🎯 Foosball backend service started!"
echo "📊 API available at: http://localhost:8080/api/foosball"
echo "🔍 Spring Data REST at: http://localhost:8080/api"
echo "📈 Actuator at: http://localhost:8080/actuator"
