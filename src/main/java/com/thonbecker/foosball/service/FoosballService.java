package com.thonbecker.foosball.service;

import com.thonbecker.foosball.entity.Game;
import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.projection.PlayerStats;
import com.thonbecker.foosball.projection.TeamStats;
import com.thonbecker.foosball.repository.GameRepository;
import com.thonbecker.foosball.repository.PlayerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Game recordGame(
            Player whiteTeamPlayer1,
            Player whiteTeamPlayer2,
            Player blackTeamPlayer1,
            Player blackTeamPlayer2,
            int whiteTeamScore,
            int blackTeamScore) {
        Game game = new Game(whiteTeamPlayer1, whiteTeamPlayer2, blackTeamPlayer1, blackTeamPlayer2);
        game.setScores(whiteTeamScore, blackTeamScore);
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

    public List<PlayerStats> getAllPlayerStatsOrderedByRankScore() {
        return playerRepository.findAllPlayerStatsOrderedByRankScore();
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByTotalGames() {
        return playerRepository.findAllPlayerStatsOrderedByTotalGames();
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByWins() {
        return playerRepository.findAllPlayerStatsOrderedByWins();
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

    public Integer getHighestTotalScore() {
        return gameRepository.getHighestTotalScore();
    }

    public Integer getLowestTotalScore() {
        return gameRepository.getLowestTotalScore();
    }
}
