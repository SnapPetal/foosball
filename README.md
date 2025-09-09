# ⚽ Foosball Backend Service

A comprehensive backend service for tracking foosball (table soccer) game results, player statistics, and team performance. Built with Spring Boot, Spring Data JPA, and Spring Data REST.

## ✨ Features

- **Player Management**: Create and manage foosball players
- **Game Recording**: Record games with team scores
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

### Option 1: Development (H2 Database)
```bash
# Start with dev profile (H2 in-memory database)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 2: PostgreSQL with Docker
```bash
# Start PostgreSQL
docker-compose up -d postgres

# Start Spring Boot (default profile)
mvn spring-boot:run
```

### Option 3: Manual Start
```bash
# Start PostgreSQL
docker-compose up -d postgres

# Start Spring Boot (default profile)
mvn spring-boot:run

# Or with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🌐 API Endpoints

### Local Development
- **Base URL**: `http://localhost:8080`
- **Health Check**: `http://localhost:8080/actuator/health`
- **API Base**: `http://localhost:8080/api/foosball`

### Player Management
- **Get All Players**: `GET /api/foosball/players`
- **Get Player by ID**: `GET /api/foosball/players/{id}`
- **Search Players**: `GET /api/foosball/players/search?name={name}`
- **Create Player**: `POST /api/foosball/players`
  ```json
  {
    "name": "Player Name",
    "email": "player@example.com"
  }
  ```

### Game Management
- **Get All Games**: `GET /api/foosball/games`
- **Get Game by ID**: `GET /api/foosball/games/{id}`
- **Get Recent Games**: `GET /api/foosball/games/recent`
- **Record Basic Game**: `POST /api/foosball/games`
  ```json
  {
    "whiteTeamPlayer1": "Alice",
    "whiteTeamPlayer2": "Bob",
    "blackTeamPlayer1": "Charlie",
    "blackTeamPlayer2": "Diana",
    "whiteTeamScore": 5,
    "blackTeamScore": 3
  }
  ```

### Player Statistics
- **All Player Stats**: `GET /api/foosball/stats/players/all`
- **Top Players by Win %**: `GET /api/foosball/stats/players/top-win-percentage?minGames=5`
- **Top Players by Total Games**: `GET /api/foosball/stats/players/top-total-games?minGames=5`
- **Top Players by Wins**: `GET /api/foosball/stats/players/top-wins?minGames=5`

### Team Statistics
- **All Team Stats**: `GET /api/foosball/stats/teams/all`
- **Top Teams by Win %**: `GET /api/foosball/stats/teams/top-win-percentage?minGames=5`
- **Top Teams by Average Score**: `GET /api/foosball/stats/teams/top-average-score?minGames=5`

### Overview Statistics
- **Game Overview**: `GET /api/foosball/stats/overview`
  Returns comprehensive statistics including:
  - Total games, players, wins, draws
  - Average scores
  - Highest/lowest scoring games

## 📊 Data Models

### Player Entity
- Basic info (name, email)
- Creation timestamp
- Relationships to games

### Game Entity
- Team composition (4 players)
- Total scores
- Game metadata (duration, notes)
- Winner determination

### Statistics Views
- **Player Stats**: Games played, wins, win percentage
- **Team Stats**: Player pairing performance

## 🗄️ Database Schema

### Tables
- `players` - Player information
- `games` - Game results

### Views
- `player_stats` - Player performance statistics (games, wins, win percentage)
- `team_stats` - Team performance metrics (win percentage, average scores)

### Database Features
- **Automatic Winner Calculation**: Games automatically determine winners based on scores
- **Team Performance**: Analysis of player pairing effectiveness
- **Comprehensive Statistics**: Multiple leaderboards and performance metrics

### Schema Strategy
- **Default**: Uses `foosball` schema
- **Dev**: PostgreSQL with auto-creation
- **Production**: PostgreSQL with schema management

## 🔧 Configuration Profiles

### Default Profile
- PostgreSQL database
- Liquibase migrations
- Production-ready settings

### Dev Profile
- PostgreSQL database (Docker)
- Detailed logging
- DevTools enabled
- Sample data loading

### Test Profile
- PostgreSQL database (Docker)
- Test-specific settings

## 🛠️ Development

### Recent Fixes & Improvements
- **CORS Configuration**: Fixed CORS issues for frontend integration
- **Circular Reference Resolution**: Resolved infinite JSON recursion in Player/Game entities
- **Database Schema Updates**: Fixed missing `win_percentage` column in team statistics
- **Liquibase Migration**: Proper database schema management with new changesets

### Development Features
- **Hot Reloading**: Spring Boot DevTools
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

### API Testing
A Postman collection is available for testing the API endpoints. You can import the `foosball.postman_collection.json` file into Postman to get started.

The collection includes requests for all major endpoints, including:
- Player management
- Game recording
- Statistics
- Health checks

```bash
# Example: Test the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Test endpoints using the Postman collection or curl
curl http://localhost:8080/api/foosball/stats/overview
curl http://localhost:8080/api/foosball/players
curl http://localhost:8080/api/foosball/games

# Create a test player
curl -X POST http://localhost:8080/api/foosball/players \
  -H "Content-Type: application/json" \
  -d '{"name":"TestPlayer","email":"test@example.com"}'
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
- Review the API documentation
- Check the health endpoints
- Review application logs

## ✅ Current Status

The application is fully functional with all endpoints working correctly:

- ✅ **Player Management**: Full CRUD operations working
- ✅ **Game Recording**: Basic game recording is working.
- ✅ **Statistics**: All statistical endpoints returning correct data.
- ✅ **Database**: Proper schema with all views and relationships
- ✅ **API**: Clean JSON responses without circular references
- ✅ **CORS**: Properly configured for frontend integration

### Verified Endpoints
All API endpoints have been tested and verified to return correct data:
- Player endpoints (4)
- Game endpoints (4) 
- Statistics endpoints (8+)
- Health and monitoring endpoints

---

**Happy Foosballing! ⚽🏆**
