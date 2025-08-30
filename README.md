# 🏓 Foosball Backend Service

A comprehensive backend service for tracking foosball game results, player statistics, and team performance. Built with Spring Boot, Spring Data JPA, and Spring Data REST.

## ✨ Features

- **Player Management**: Create and manage foosball players
- **Game Recording**: Record games with team scores and position-based scoring
- **Position Analysis**: Track goalie vs. forward performance
- **Team Performance**: Analyze how players perform together
- **Comprehensive Statistics**: Multiple leaderboards and performance metrics
- **RESTful API**: Full REST API with Spring Data REST
- **Database Migrations**: Liquibase for schema management
- **Multiple Profiles**: Development, testing, and production configurations

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- Docker & Docker Compose (for PostgreSQL)

### Option 1: Local Development (H2 Database)
```bash
# Make scripts executable
chmod +x start-local.sh

# Start with local profile (H2 in-memory database)
./start-local.sh
```

### Option 2: PostgreSQL with Docker
```bash
# Make scripts executable
chmod +x start.sh

# Start PostgreSQL and Spring Boot
./start.sh
```

### Option 3: Manual Start
```bash
# Start PostgreSQL
docker-compose up -d postgres

# Start Spring Boot (default profile)
mvn spring-boot:run

# Or with local profile
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## 🌐 API Endpoints

### Core Endpoints
- **Players**: `/api/players` - Player management
- **Games**: `/api/games` - Game recording and retrieval
- **Statistics**: `/api/foosball/stats/*` - Various statistics endpoints

### Spring Data REST
- **Players**: `/api/players` - Full CRUD operations
- **Games**: `/api/games` - Full CRUD operations
- **Search**: `/api/players/search?name=Alice` - Player search

### Custom API Endpoints
- **Create Player**: `POST /api/foosball/players`
- **Record Game**: `POST /api/foosball/games`
- **Position Game**: `POST /api/foosball/games/position-record`
- **Player Stats**: `GET /api/foosball/stats/players/*`
- **Position Stats**: `GET /api/foosball/stats/position/*`
- **Team Stats**: `GET /api/foosball/stats/teams/*`
- **Overview**: `GET /api/foosball/stats/overview`

## 📊 Data Models

### Player Entity
- Basic info (name, email)
- Creation timestamp
- Relationships to games

### Game Entity
- Team composition (4 players)
- Total scores
- Position-based scores (goalie/forward)
- Game metadata (duration, notes)
- Winner determination

### Statistics Views
- **Player Stats**: Games played, wins, win percentage
- **Position Stats**: Goalie vs. forward performance
- **Team Stats**: Player pairing performance

## 🗄️ Database Schema

### Tables
- `players` - Player information
- `games` - Game results with position scoring

### Views
- `player_stats` - Player performance statistics
- `position_stats` - Position-based performance
- `team_stats` - Team performance metrics

### Schema Strategy
- **Default**: Uses `foosball` schema
- **Local**: H2 in-memory with auto-creation
- **Production**: PostgreSQL with schema management

## 🔧 Configuration Profiles

### Default Profile
- PostgreSQL database
- Liquibase migrations
- Production-ready settings

### Local Profile
- H2 in-memory database
- H2 console enabled
- Detailed logging
- DevTools enabled
- Sample data loading

### Test Profile
- H2 in-memory database
- Test-specific settings

## 🛠️ Development

### Local Development Features
- **Hot Reloading**: Spring Boot DevTools
- **H2 Console**: Database inspection at `/h2-console`
- **Detailed Logging**: SQL queries, web requests, transactions
- **Sample Data**: Automatic loading of test data
- **CORS**: Full CORS support for frontend development

### Database Migrations
- Liquibase changelog management
- Schema creation and updates
- View and index management

### Testing
```bash
# Run tests
mvn test

# Run with specific profile
mvn test -Dspring.profiles.active=test
```

## 📈 Monitoring

### Actuator Endpoints
- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Environment**: `/actuator/env`

### Health Checks
- Database connectivity
- Application status
- Custom health indicators

## 🚀 Production Deployment

### PostgreSQL Setup
1. Create database and user
2. Set environment variables
3. Run Liquibase migrations
4. Configure connection pooling

### Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db:5432/foosball
SPRING_DATASOURCE_USERNAME=your_user
SPRING_DATASOURCE_PASSWORD=your_password
FOOSBALL_SCHEMA_NAME=foosball
```

### Docker Deployment
```bash
# Build image
mvn clean package

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/foosball \
  foosball:latest
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 🆘 Support

For questions and support:
- Check the [DEVELOPMENT.md](DEVELOPMENT.md) guide
- Review the API documentation
- Check the health endpoints
- Review application logs

---

**Happy Foosballing! 🏓⚽**
