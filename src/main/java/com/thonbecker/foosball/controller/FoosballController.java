package com.thonbecker.foosball.controller;

import com.thonbecker.foosball.entity.Game;
import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.model.CreatePlayerRequest;
import com.thonbecker.foosball.model.GameRequest;
import com.thonbecker.foosball.projection.PlayerStats;
import com.thonbecker.foosball.projection.TeamStats;
import com.thonbecker.foosball.service.FoosballService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/foosball")
@CrossOrigin(
        origins = {"http://localhost:3000", "http://localhost:8080"},
        allowCredentials = "false")
public class FoosballController {

    private final FoosballService foosballService;

    @Autowired
    public FoosballController(FoosballService foosballService) {
        this.foosballService = foosballService;
    }

    // Player endpoints
    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody CreatePlayerRequest request) {
        Player player = foosballService.createPlayer(request.name(), request.email());
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
                foosballService.findPlayerByName(request.whiteTeamPlayer1()).orElse(null);
        Player whiteTeamPlayer2 =
                foosballService.findPlayerByName(request.whiteTeamPlayer2()).orElse(null);
        Player blackTeamPlayer1 =
                foosballService.findPlayerByName(request.blackTeamPlayer1()).orElse(null);
        Player blackTeamPlayer2 =
                foosballService.findPlayerByName(request.blackTeamPlayer2()).orElse(null);

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
                request.whiteTeamScore(),
                request.blackTeamScore());
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
    public ResponseEntity<List<PlayerStats>> getTopPlayersByTotalGames(
            @RequestParam(defaultValue = "5") int minGames) {
        List<PlayerStats> stats = foosballService.getTopPlayersByTotalGames(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/players/top-wins")
    public ResponseEntity<List<PlayerStats>> getTopPlayersByWins(
            @RequestParam(defaultValue = "5") int minGames) {
        List<PlayerStats> stats = foosballService.getTopPlayersByWins(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/players/all")
    public ResponseEntity<List<PlayerStats>> getAllPlayerStats() {
        List<PlayerStats> stats = foosballService.getAllPlayerStatsOrderedByRankScore();
        return ResponseEntity.ok(stats);
    }

    // Team performance statistics
    @GetMapping("/stats/teams/top-win-percentage")
    public ResponseEntity<List<TeamStats>> getTopTeamsByWinPercentage(
            @RequestParam(defaultValue = "5") int minGames) {
        List<TeamStats> stats = foosballService.getTopTeamsByWinPercentage(minGames);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/teams/top-average-score")
    public ResponseEntity<List<TeamStats>> getTopTeamsByAverageScore(
            @RequestParam(defaultValue = "5") int minGames) {
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
                Map.entry("highestTotalScore", foosballService.getHighestTotalScore()),
                Map.entry("lowestTotalScore", foosballService.getLowestTotalScore()));
        return ResponseEntity.ok(stats);
    }
}
