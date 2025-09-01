# 🚀 Foosball API Reference

Complete API documentation for the Foosball Backend Service.

## 📋 Base Information

- **Base URL**: `http://localhost:8080`
- **API Base**: `/api/foosball`
- **Content-Type**: `application/json`
- **CORS**: Enabled for localhost:3000 and localhost:8080

## 🔐 Authentication

Currently, no authentication is required for API endpoints.

## 📊 Response Format

All endpoints return JSON responses. Error responses include:
```json
{
  "timestamp": "2025-08-31T19:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description",
  "path": "/api/foosball/endpoint"
}
```

## 👥 Player Management

### Get All Players
```http
GET /api/foosball/players
```

**Response**: Array of players with game relationships
```json
[
  {
    "id": 1,
    "name": "Alice",
    "email": "alice@example.com",
    "createdAt": "2025-08-31T17:45:25.824955",
    "whiteTeamPlayer1Games": [...],
    "whiteTeamPlayer2Games": [...],
    "blackTeamPlayer1Games": [...],
    "blackTeamPlayer2Games": [...]
  }
]
```

### Get Player by ID
```http
GET /api/foosball/players/{id}
```

**Response**: Single player object or 404 if not found

### Search Players
```http
GET /api/foosball/players/search?name={name}
```

**Parameters**:
- `name` (required): Player name to search for

### Create Player
```http
POST /api/foosball/players
```

**Request Body**:
```json
{
  "name": "Player Name",
  "email": "player@example.com"
}
```

**Response**: Created player object with generated ID

## 🎮 Game Management

### Get All Games
```http
GET /api/foosball/games
```

**Response**: Array of games with calculated statistics
```json
[
  {
    "id": 1,
    "whiteTeamScore": 5,
    "blackTeamScore": 5,
    "whiteTeamGoalieScore": 3,
    "whiteTeamForwardScore": 2,
    "blackTeamGoalieScore": 1,
    "blackTeamForwardScore": 4,
    "winner": null,
    "playedAt": "2025-08-31T17:45:25.900864",
    "gameDurationMinutes": 15,
    "notes": "Great game, very close!",
    "totalGoalieScore": 4,
    "totalForwardScore": 6,
    "draw": true,
    "whiteTeamWinner": false,
    "blackTeamWinner": false,
    "highestScoringPosition": "FORWARD"
  }
]
```

### Get Game by ID
```http
GET /api/foosball/games/{id}
```

### Get Recent Games
```http
GET /api/foosball/games/recent
```

### Record Basic Game
```http
POST /api/foosball/games
```

**Request Body**:
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

**Notes**:
- Player names must exist in the database
- Winner is automatically calculated
- Position scores default to 0

### Record Position Game
```http
POST /api/foosball/games/position-record
```

**Request Body**:
```json
{
  "whiteTeamPlayer1": "Alice",
  "whiteTeamPlayer2": "Bob",
  "blackTeamPlayer1": "Charlie",
  "blackTeamPlayer2": "Diana",
  "whiteGoalieScore": 3,
  "whiteForwardScore": 2,
  "blackGoalieScore": 1,
  "blackForwardScore": 2,
  "gameDurationMinutes": 20,
  "notes": "Great game!"
}
```

**Notes**:
- `gameDurationMinutes` and `notes` are optional
- Total scores are automatically calculated from position scores
- Winner is automatically determined

## 📈 Player Statistics

### All Player Stats
```http
GET /api/foosball/stats/players/all
```

**Response**: Players ordered by win percentage
```json
[
  {
    "totalGames": 3,
    "wins": 2,
    "winPercentage": 66.66666666666667,
    "losses": 1,
    "formattedWinPercentage": "66.7%",
    "name": "Eve",
    "id": 5
  }
]
```

### Top Players by Win Percentage
```http
GET /api/foosball/stats/players/top-win-percentage?minGames=5
```

**Parameters**:
- `minGames` (optional): Minimum games required (default: 5)

### Top Players by Total Games
```http
GET /api/foosball/stats/players/top-total-games?minGames=5
```

### Top Players by Wins
```http
GET /api/foosball/stats/players/top-wins?minGames=5
```

## 🎯 Position Statistics

### All Position Stats
```http
GET /api/foosball/stats/position/all
```

**Response**: Players ordered by total goals
```json
[
  {
    "totalGames": 4,
    "totalGoalieGoals": 4,
    "totalForwardGoals": 7,
    "totalGoals": 11,
    "goalsPerGame": 2,
    "preferredPosition": "Forward",
    "goalieEfficiency": 1.0,
    "forwardEfficiency": 1.75,
    "name": "Alice",
    "id": 1
  }
]
```

### Top Scorers by Total Goals
```http
GET /api/foosball/stats/position/top-scorers?minGames=5
```

### Top Goalie Scorers
```http
GET /api/foosball/stats/position/top-goalies?minGames=5
```

### Top Forward Scorers
```http
GET /api/foosball/stats/position/top-forwards?minGames=5
```

## 🏆 Team Statistics

### All Team Stats
```http
GET /api/foosball/stats/teams/all
```

**Response**: Teams ordered by win percentage
```json
[
  {
    "winPercentage": 75.0,
    "formattedWinPercentage": "75.0%",
    "player1Id": 5,
    "player1Name": "Eve",
    "player2Id": 6,
    "player2Name": "Frank",
    "gamesPlayedTogether": 4,
    "winsTogether": 3,
    "averageTeamScore": 5.0,
    "lossesTogether": 1,
    "teamName": "Eve & Frank",
    "performanceRating": "Strong"
  }
]
```

### Top Teams by Win Percentage
```http
GET /api/foosball/stats/teams/top-win-percentage?minGames=5
```

### Top Teams by Average Score
```http
GET /api/foosball/stats/teams/top-average-score?minGames=5
```

## 📊 Overview Statistics

### Game Overview
```http
GET /api/foosball/stats/overview
```

**Response**: Comprehensive game statistics
```json
{
  "totalPlayers": 7,
  "averageGameDuration": 15.833333333333334,
  "averageForwardGoalsPerGame": 4.285714285714286,
  "totalGames": 7,
  "gamesWithWinner": 5,
  "averageTotalScore": 8.857142857142858,
  "highestTotalScore": 10,
  "draws": 2,
  "lowestTotalScore": 8,
  "mostScoringPosition": "FORWARD",
  "averageGoalieGoalsPerGame": 3.4285714285714284
}
```

## 🔍 Search and Filtering

### Player Search
```http
GET /api/foosball/players/search?name={name}
```

**Parameters**:
- `name` (required): Partial player name (case-insensitive)

### Statistics Filtering
Most statistics endpoints support `minGames` parameter to filter results by minimum games played.

## 🚨 Error Handling

### Common HTTP Status Codes
- `200 OK`: Successful request
- `400 Bad Request`: Invalid request data
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### Validation Errors
When creating players or games, validation errors return 400 with details about what fields failed validation.

## 🧪 Testing Examples

### Test Player Creation
```bash
curl -X POST http://localhost:8080/api/foosball/players \
  -H "Content-Type: application/json" \
  -d '{"name":"TestPlayer","email":"test@example.com"}'
```

### Test Game Creation
```bash
curl -X POST http://localhost:8080/api/foosball/games \
  -H "Content-Type: application/json" \
  -d '{
    "whiteTeamPlayer1":"Alice",
    "whiteTeamPlayer2":"Bob",
    "blackTeamPlayer1":"Charlie",
    "blackTeamPlayer2":"Diana",
    "whiteTeamScore":5,
    "blackTeamScore":3
  }'
```

### Test Statistics
```bash
curl http://localhost:8080/api/foosball/stats/overview
curl http://localhost:8080/api/foosball/stats/players/all
curl http://localhost:8080/api/foosball/stats/teams/all
```

## 📝 Notes

- All timestamps are in ISO 8601 format
- Player names are used for game creation (not IDs)
- Games automatically calculate winners and statistics
- Position-based scoring is optional but provides richer analytics
- Team statistics are calculated for all possible player combinations
- Performance ratings are automatically assigned based on win percentages

---

For more information, see the main [README.md](README.md) file.
