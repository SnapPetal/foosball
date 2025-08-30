package com.thonbecker.foosball.controller;

import com.thonbecker.foosball.entity.Game;
import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.projection.PlayerStats;
import com.thonbecker.foosball.projection.PositionStats;
import com.thonbecker.foosball.projection.TeamStats;
import com.thonbecker.foosball.service.FoosballService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foosball")
@CrossOrigin(origins = "*")
public class FoosballController {

    private final FoosballService foosballService;

    @Autowired
    public FoosballController(FoosballService foosballService) {
        this.foosballService = foosballService;
    }

    // Player endpoints
    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody CreatePlayerRequest request) {
        Player player = foosballService.createPlayer(request.getName(), request.getEmail());
        return ResponseEntity.ok(player);
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = foosballService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return foosballService.getAllPlayers().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/players/search")
    public ResponseEntity<List<Player>> searchPlayers(@RequestParam String name) {
        List<Player> players = foosballService.getAllPlayers().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        return ResponseEntity.ok(players);
    }

    // Game endpoints
    @PostMapping("/games")
    public ResponseEntity<Game> recordGame(@RequestBody GameRequest request) {
        Player whiteTeamPlayer1 =
                foosballService.findPlayerByName(request.getWhiteTeamPlayer1()).orElse(null);
        Player whiteTeamPlayer2 =
                foosballService.findPlayerByName(request.getWhiteTeamPlayer2()).orElse(null);
        Player blackTeamPlayer1 =
                foosballService.findPlayerByName(request.getBlackTeamPlayer1()).orElse(null);
        Player blackTeamPlayer2 =
                foosballService.findPlayerByName(request.getBlackTeamPlayer2()).orElse(null);

        if (whiteTeamPlayer1 == null
                || whiteTeamPlayer2 == null
                || blackTeamPlayer1 == null
                || blackTeamPlayer2 == null) {
            return ResponseEntity.badRequest().build();
        }

        Game game = foosballService.recordGame(
                whiteTeamPlayer1,
                whiteTeamPlayer2,
                blackTeamPlayer1,
                blackTeamPlayer2,
                request.getWhiteTeamScore(),
                request.getBlackTeamScore());
        return ResponseEntity.ok(game);
    }

    @PostMapping("/games/position-record")
    public ResponseEntity<Game> recordGameWithPositions(@RequestBody PositionGameRequest request) {
        Player whiteTeamPlayer1 =
                foosballService.findPlayerByName(request.getWhiteTeamPlayer1()).orElse(null);
        Player whiteTeamPlayer2 =
                foosballService.findPlayerByName(request.getWhiteTeamPlayer2()).orElse(null);
        Player blackTeamPlayer1 =
                foosballService.findPlayerByName(request.getBlackTeamPlayer1()).orElse(null);
        Player blackTeamPlayer2 =
                foosballService.findPlayerByName(request.getBlackTeamPlayer2()).orElse(null);

        if (whiteTeamPlayer1 == null
                || whiteTeamPlayer2 == null
                || blackTeamPlayer1 == null
                || blackTeamPlayer2 == null) {
            return ResponseEntity.badRequest().build();
        }

        Game game = foosballService.recordGameWithPositionScores(
                whiteTeamPlayer1,
                whiteTeamPlayer2,
                blackTeamPlayer1,
                blackTeamPlayer2,
                request.getWhiteGoalieScore(),
                request.getWhiteForwardScore(),
                request.getBlackGoalieScore(),
                request.getBlackForwardScore(),
                request.getGameDurationMinutes(),
                request.getNotes());
        return ResponseEntity.ok(game);
    }

    @GetMapping("/games")
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = foosballService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/games/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        return foosballService
                .getGameById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/games/recent")
    public ResponseEntity<List<Game>> getRecentGames() {
        List<Game> games = foosballService.getRecentGames();
        return ResponseEntity.ok(games);
    }

    // Statistics endpoints
    @GetMapping("/stats/players/top-win-percentage")
    public ResponseEntity<List<PlayerStats>> getTopPlayersByWinPercentage(
            @RequestParam(defaultValue = "5") int minGames) {
        List<PlayerStats> stats = foosballService.getTopPlayersByWinPercentage(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/players/top-total-games")
    public ResponseEntity<List<PlayerStats>> getTopPlayersByTotalGames(@RequestParam(defaultValue = "5") int minGames) {
        List<PlayerStats> stats = foosballService.getTopPlayersByTotalGames(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/players/top-wins")
    public ResponseEntity<List<PlayerStats>> getTopPlayersByWins(@RequestParam(defaultValue = "5") int minGames) {
        List<PlayerStats> stats = foosballService.getTopPlayersByWins(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/players/all")
    public ResponseEntity<List<PlayerStats>> getAllPlayerStats() {
        List<PlayerStats> stats = foosballService.getAllPlayerStatsOrderedByWinPercentage();
        return ResponseEntity.ok(stats);
    }

    // Position-based statistics
    @GetMapping("/stats/position/top-scorers")
    public ResponseEntity<List<PositionStats>> getTopScorersByTotalGoals(
            @RequestParam(defaultValue = "5") int minGames) {
        List<PositionStats> stats = foosballService.getTopScorersByTotalGoals(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/position/top-goalies")
    public ResponseEntity<List<PositionStats>> getTopGoalieScorers(@RequestParam(defaultValue = "5") int minGames) {
        List<PositionStats> stats = foosballService.getTopGoalieScorers(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/position/top-forwards")
    public ResponseEntity<List<PositionStats>> getTopForwardScorers(@RequestParam(defaultValue = "5") int minGames) {
        List<PositionStats> stats = foosballService.getTopForwardScorers(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/position/all")
    public ResponseEntity<List<PositionStats>> getAllPositionStats() {
        List<PositionStats> stats = foosballService.getAllPositionStatsOrderedByTotalGoals();
        return ResponseEntity.ok(stats);
    }

    // Team performance statistics
    @GetMapping("/stats/teams/top-win-percentage")
    public ResponseEntity<List<TeamStats>> getTopTeamsByWinPercentage(@RequestParam(defaultValue = "5") int minGames) {
        List<TeamStats> stats = foosballService.getTopTeamsByWinPercentage(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/teams/top-average-score")
    public ResponseEntity<List<TeamStats>> getTopTeamsByAverageScore(@RequestParam(defaultValue = "5") int minGames) {
        List<TeamStats> stats = foosballService.getTopTeamsByAverageScore(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/teams/all")
    public ResponseEntity<List<TeamStats>> getAllTeamStats() {
        List<TeamStats> stats = foosballService.getAllTeamStatsOrderedByWinPercentage();
        return ResponseEntity.ok(stats);
    }

    // Overview statistics
    @GetMapping("/stats/overview")
    public ResponseEntity<Map<String, Object>> getGameStatsOverview() {
        Map<String, Object> stats = Map.ofEntries(
                Map.entry("totalGames", foosballService.getTotalGames()),
                Map.entry("totalPlayers", foosballService.getTotalPlayers()),
                Map.entry("gamesWithWinner", foosballService.getGamesWithWinner()),
                Map.entry("draws", foosballService.getDraws()),
                Map.entry("averageTotalScore", foosballService.getAverageTotalScore()),
                Map.entry("averageGameDuration", foosballService.getAverageGameDuration()),
                Map.entry("highestTotalScore", foosballService.getHighestTotalScore()),
                Map.entry("lowestTotalScore", foosballService.getLowestTotalScore()),
                Map.entry("mostScoringPosition", foosballService.getMostScoringPosition()),
                Map.entry("averageGoalieGoalsPerGame", foosballService.getAverageGoalieGoalsPerGame()),
                Map.entry("averageForwardGoalsPerGame", foosballService.getAverageForwardGoalsPerGame()));
        return ResponseEntity.ok(stats);
    }

    // Request DTOs
    public static class CreatePlayerRequest {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class GameRequest {
        private String whiteTeamPlayer1;
        private String whiteTeamPlayer2;
        private String blackTeamPlayer1;
        private String blackTeamPlayer2;
        private int whiteTeamScore;
        private int blackTeamScore;

        public String getWhiteTeamPlayer1() {
            return whiteTeamPlayer1;
        }

        public void setWhiteTeamPlayer1(String whiteTeamPlayer1) {
            this.whiteTeamPlayer1 = whiteTeamPlayer1;
        }

        public String getWhiteTeamPlayer2() {
            return whiteTeamPlayer2;
        }

        public void setWhiteTeamPlayer2(String whiteTeamPlayer2) {
            this.whiteTeamPlayer2 = whiteTeamPlayer2;
        }

        public String getBlackTeamPlayer1() {
            return blackTeamPlayer1;
        }

        public void setBlackTeamPlayer1(String blackTeamPlayer1) {
            this.blackTeamPlayer1 = blackTeamPlayer1;
        }

        public String getBlackTeamPlayer2() {
            return blackTeamPlayer2;
        }

        public void setBlackTeamPlayer2(String blackTeamPlayer2) {
            this.blackTeamPlayer2 = blackTeamPlayer2;
        }

        public int getWhiteTeamScore() {
            return whiteTeamScore;
        }

        public void setWhiteTeamScore(int whiteTeamScore) {
            this.whiteTeamScore = whiteTeamScore;
        }

        public int getBlackTeamScore() {
            return blackTeamScore;
        }

        public void setBlackTeamScore(int blackTeamScore) {
            this.blackTeamScore = blackTeamScore;
        }
    }

    public static class PositionGameRequest {
        private String whiteTeamPlayer1;
        private String whiteTeamPlayer2;
        private String blackTeamPlayer1;
        private String blackTeamPlayer2;
        private int whiteGoalieScore;
        private int whiteForwardScore;
        private int blackGoalieScore;
        private int blackForwardScore;
        private Integer gameDurationMinutes;
        private String notes;

        public String getWhiteTeamPlayer1() {
            return whiteTeamPlayer1;
        }

        public void setWhiteTeamPlayer1(String whiteTeamPlayer1) {
            this.whiteTeamPlayer1 = whiteTeamPlayer1;
        }

        public String getWhiteTeamPlayer2() {
            return whiteTeamPlayer2;
        }

        public void setWhiteTeamPlayer2(String whiteTeamPlayer2) {
            this.whiteTeamPlayer2 = whiteTeamPlayer2;
        }

        public String getBlackTeamPlayer1() {
            return blackTeamPlayer1;
        }

        public void setBlackTeamPlayer1(String blackTeamPlayer1) {
            this.blackTeamPlayer1 = blackTeamPlayer1;
        }

        public String getBlackTeamPlayer2() {
            return blackTeamPlayer2;
        }

        public void setBlackTeamPlayer2(String blackTeamPlayer2) {
            this.blackTeamPlayer2 = blackTeamPlayer2;
        }

        public int getWhiteGoalieScore() {
            return whiteGoalieScore;
        }

        public void setWhiteGoalieScore(int whiteGoalieScore) {
            this.whiteGoalieScore = whiteGoalieScore;
        }

        public int getWhiteForwardScore() {
            return whiteForwardScore;
        }

        public void setWhiteForwardScore(int whiteForwardScore) {
            this.whiteForwardScore = whiteForwardScore;
        }

        public int getBlackGoalieScore() {
            return blackGoalieScore;
        }

        public void setBlackGoalieScore(int blackGoalieScore) {
            this.blackGoalieScore = blackGoalieScore;
        }

        public int getBlackForwardScore() {
            return blackForwardScore;
        }

        public void setBlackForwardScore(int blackForwardScore) {
            this.blackForwardScore = blackForwardScore;
        }

        public Integer getGameDurationMinutes() {
            return gameDurationMinutes;
        }

        public void setGameDurationMinutes(Integer gameDurationMinutes) {
            this.gameDurationMinutes = gameDurationMinutes;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
