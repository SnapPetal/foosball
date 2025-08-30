package com.thonbecker.foosball.service;

import com.thonbecker.foosball.entity.Game;
import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.projection.PlayerStats;
import com.thonbecker.foosball.projection.PositionStats;
import com.thonbecker.foosball.projection.TeamStats;
import com.thonbecker.foosball.repository.GameRepository;
import com.thonbecker.foosball.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FoosballService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Autowired
    public FoosballService(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    // Player management
    public Player createPlayer(String name, String email) {
        Player player = new Player(name, email);
        return playerRepository.save(player);
    }

    public Player createPlayer(String name) {
        Player player = new Player(name);
        return playerRepository.save(player);
    }

    public Optional<Player> findPlayerByName(String name) {
        return playerRepository.findByName(name);
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // Game management
    public Game recordGame(Player whiteTeamPlayer1, Player whiteTeamPlayer2,
                          Player blackTeamPlayer1, Player blackTeamPlayer2,
                          int whiteTeamScore, int blackTeamScore) {
        Game game = new Game(whiteTeamPlayer1, whiteTeamPlayer2, blackTeamPlayer1, blackTeamPlayer2);
        game.setScores(whiteTeamScore, blackTeamScore);
        return gameRepository.save(game);
    }

    public Game recordGameWithPositionScores(Player whiteTeamPlayer1, Player whiteTeamPlayer2,
                                           Player blackTeamPlayer1, Player blackTeamPlayer2,
                                           int whiteGoalieScore, int whiteForwardScore,
                                           int blackGoalieScore, int blackForwardScore,
                                           Integer gameDurationMinutes, String notes) {
        Game game = new Game(whiteTeamPlayer1, whiteTeamPlayer2, blackTeamPlayer1, blackTeamPlayer2);
        game.setPositionScores(whiteGoalieScore, whiteForwardScore, blackGoalieScore, blackForwardScore);
        game.setGameDurationMinutes(gameDurationMinutes);
        game.setNotes(notes);
        return gameRepository.save(game);
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    public List<Game> getGamesByPlayer(Player player) {
        return gameRepository.findByPlayer(player);
    }

    public List<Game> getRecentGames() {
        return gameRepository.findRecentGames();
    }

    // Player statistics
    public List<PlayerStats> getTopPlayersByWinPercentage(int minGames) {
        return playerRepository.findTopPlayersByWinPercentage(minGames);
    }

    public List<PlayerStats> getTopPlayersByTotalGames(int minGames) {
        return playerRepository.findTopPlayersByTotalGames(minGames);
    }

    public List<PlayerStats> getTopPlayersByWins(int minGames) {
        return playerRepository.findTopPlayersByWins(minGames);
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByWinPercentage() {
        return playerRepository.findAllPlayerStatsOrderedByWinPercentage();
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByTotalGames() {
        return playerRepository.findAllPlayerStatsOrderedByTotalGames();
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByWins() {
        return playerRepository.findAllPlayerStatsOrderedByWins();
    }

    // Position-based statistics
    public List<PositionStats> getTopScorersByTotalGoals(int minGames) {
        return playerRepository.findTopScorersByTotalGoals(minGames);
    }

    public List<PositionStats> getTopGoalieScorers(int minGames) {
        return playerRepository.findTopGoalieScorers(minGames);
    }

    public List<PositionStats> getTopForwardScorers(int minGames) {
        return playerRepository.findTopForwardScorers(minGames);
    }

    public List<PositionStats> getAllPositionStatsOrderedByTotalGoals() {
        return playerRepository.findAllPositionStatsOrderedByTotalGoals();
    }

    public List<PositionStats> getAllPositionStatsOrderedByGoalieGoals() {
        return playerRepository.findAllPositionStatsOrderedByGoalieGoals();
    }

    public List<PositionStats> getAllPositionStatsOrderedByForwardGoals() {
        return playerRepository.findAllPositionStatsOrderedByForwardGoals();
    }

    // Team performance statistics
    public List<TeamStats> getTopTeamsByWinPercentage(int minGames) {
        return playerRepository.findTopTeamsByWinPercentage(minGames);
    }

    public List<TeamStats> getTopTeamsByAverageScore(int minGames) {
        return playerRepository.findTopTeamsByAverageScore(minGames);
    }

    public List<TeamStats> getAllTeamStatsOrderedByWinPercentage() {
        return playerRepository.findAllTeamStatsOrderedByWinPercentage();
    }

    public List<TeamStats> getAllTeamStatsOrderedByGamesPlayed() {
        return playerRepository.findAllTeamStatsOrderedByGamesPlayed();
    }

    // Overall statistics
    public Long getTotalGames() {
        return gameRepository.count();
    }

    public Long getTotalPlayers() {
        return playerRepository.count();
    }

    public Long getGamesWithWinner() {
        return gameRepository.countGamesWithWinner();
    }

    public Long getDraws() {
        return gameRepository.countDraws();
    }

    public Double getAverageTotalScore() {
        return gameRepository.getAverageTotalScore();
    }

    public Double getAverageGameDuration() {
        return gameRepository.getAverageGameDuration();
    }

    public Integer getHighestTotalScore() {
        return gameRepository.getHighestTotalScore();
    }

    public Integer getLowestTotalScore() {
        return gameRepository.getLowestTotalScore();
    }

    // Position analysis
    public Game.Position getMostScoringPosition() {
        List<Game> allGames = gameRepository.findAll();
        int totalGoalieGoals = 0;
        int totalForwardGoals = 0;

        for (Game game : allGames) {
            totalGoalieGoals += game.getTotalGoalieScore();
            totalForwardGoals += game.getTotalForwardScore();
        }

        if (totalGoalieGoals > totalForwardGoals) {
            return Game.Position.GOALIE;
        } else if (totalForwardGoals > totalGoalieGoals) {
            return Game.Position.FORWARD;
        } else {
            return null; // Tie
        }
    }

    public Double getAverageGoalieGoalsPerGame() {
        List<Game> allGames = gameRepository.findAll();
        if (allGames.isEmpty()) return 0.0;

        int totalGoalieGoals = allGames.stream()
                .mapToInt(Game::getTotalGoalieScore)
                .sum();

        return (double) totalGoalieGoals / allGames.size();
    }

    public Double getAverageForwardGoalsPerGame() {
        List<Game> allGames = gameRepository.findAll();
        if (allGames.isEmpty()) return 0.0;

        int totalForwardGoals = allGames.stream()
                .mapToInt(Game::getTotalForwardScore)
                .sum();

        return (double) totalForwardGoals / allGames.size();
    }
}
