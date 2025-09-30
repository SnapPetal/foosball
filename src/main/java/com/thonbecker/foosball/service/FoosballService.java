package com.thonbecker.foosball.service;

import com.thonbecker.foosball.entity.Game;
import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.projection.PlayerStats;
import com.thonbecker.foosball.projection.TeamStats;
import com.thonbecker.foosball.repository.GameRepository;
import com.thonbecker.foosball.repository.PlayerRepository;
import com.thonbecker.foosball.repository.PlayerStatsRepository;
import com.thonbecker.foosball.repository.TeamStatsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FoosballService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final TeamStatsRepository teamStatsRepository;

    @Autowired
    public FoosballService(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            PlayerStatsRepository playerStatsRepository,
            TeamStatsRepository teamStatsRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.teamStatsRepository = teamStatsRepository;
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
        return playerRepository.findAllByOrderByNameAsc();
    }

    // Game management
    public Game recordGame(
            Player whiteTeamPlayer1,
            Player whiteTeamPlayer2,
            Player blackTeamPlayer1,
            Player blackTeamPlayer2,
            int whiteTeamScore,
            int blackTeamScore) {
        Game game =
                new Game(whiteTeamPlayer1, whiteTeamPlayer2, blackTeamPlayer1, blackTeamPlayer2);
        game.setScores(whiteTeamScore, blackTeamScore);
        return gameRepository.save(game);
    }

    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        gameRepository.findAll().forEach(games::add);
        return games;
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
        return playerStatsRepository.findTopPlayersByWinPercentage(minGames);
    }

    public List<PlayerStats> getTopPlayersByTotalGames(int minGames) {
        return playerStatsRepository.findTopPlayersByTotalGames(minGames);
    }

    public List<PlayerStats> getTopPlayersByWins(int minGames) {
        return playerStatsRepository.findTopPlayersByWins(minGames);
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByWinPercentage() {
        return playerStatsRepository.findAllPlayerStatsOrderedByWinPercentage();
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByRankScore() {
        return playerStatsRepository.findAllPlayerStatsOrderedByRankScore();
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByTotalGames() {
        return playerStatsRepository.findAllPlayerStatsOrderedByTotalGames();
    }

    public List<PlayerStats> getAllPlayerStatsOrderedByWins() {
        return playerStatsRepository.findAllPlayerStatsOrderedByWins();
    }

    // Team performance statistics
    public List<TeamStats> getTopTeamsByWinPercentage(int minGames) {
        return teamStatsRepository.findTopTeamsByWinPercentage(minGames);
    }

    public List<TeamStats> getTopTeamsByAverageScore(int minGames) {
        return teamStatsRepository.findTopTeamsByAverageScore(minGames);
    }

    public List<TeamStats> getAllTeamStatsOrderedByWinPercentage() {
        return teamStatsRepository.findAllTeamStatsOrderedByWinPercentage();
    }

    public List<TeamStats> getAllTeamStatsOrderedByGamesPlayed() {
        return teamStatsRepository.findAllTeamStatsOrderedByGamesPlayed();
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
