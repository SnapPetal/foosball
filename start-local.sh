#!/bin/bash

echo "🚀 Starting Foosball Backend Service (Local Development Mode)..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH."
    echo "   Please install Java 21 or later."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "21" ]; then
    echo "❌ Java version $JAVA_VERSION is not supported."
    echo "   Please use Java 21 or later."
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed or not in PATH."
    echo "   Please install Maven."
    exit 1
fi

echo "✅ Maven detected"

echo "☕ Starting Spring Boot application with local profile..."
echo "   - H2 Console: http://localhost:8080/h2-console"
echo "   - API: http://localhost:8080/api/foosball"
echo "   - Spring Data REST: http://localhost:8080/api"
echo "   - Actuator: http://localhost:8080/actuator"
echo "   - DevTools: Hot reloading enabled"
echo "   - Sample data: Will be loaded automatically"
echo ""

mvn spring-boot:run -Dspring-boot.run.profiles=local

echo "🎯 Foosball backend service stopped."
