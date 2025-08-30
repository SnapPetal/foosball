package com.thonbecker.foosball.repository;

import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.projection.PlayerStats;
import com.thonbecker.foosball.projection.PositionStats;
import com.thonbecker.foosball.projection.TeamStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(
    path = "players",
    collectionResourceRel = "players",
    itemResourceRel = "player"
)
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @RestResource(path = "by-name", rel = "by-name")
    Optional<Player> findByName(String name);
    
    @RestResource(path = "by-email", rel = "by-email")
    Optional<Player> findByEmail(String email);
    
    @RestResource(path = "search", rel = "search")
    List<Player> findByNameContainingIgnoreCase(String name);
    
    // Player stats queries
    @Query(value = "SELECT * FROM foosball.player_stats ORDER BY win_percentage DESC, total_games DESC", nativeQuery = true)
    List<PlayerStats> findAllPlayerStatsOrderedByWinPercentage();
    
    @Query(value = "SELECT * FROM foosball.player_stats ORDER BY total_games DESC, win_percentage DESC", nativeQuery = true)
    List<PlayerStats> findAllPlayerStatsOrderedByTotalGames();
    
    @Query(value = "SELECT * FROM foosball.player_stats ORDER BY wins DESC, win_percentage DESC", nativeQuery = true)
    List<PlayerStats> findAllPlayerStatsOrderedByWins();
    
    @Query(value = "SELECT * FROM foosball.player_stats WHERE total_games >= :minGames ORDER BY win_percentage DESC", nativeQuery = true)
    List<PlayerStats> findTopPlayersByWinPercentage(int minGames);
    
    @Query(value = "SELECT * FROM foosball.player_stats WHERE total_games >= :minGames ORDER BY total_games DESC", nativeQuery = true)
    List<PlayerStats> findTopPlayersByTotalGames(int minGames);
    
    @Query(value = "SELECT * FROM foosball.player_stats WHERE total_games >= :minGames ORDER BY wins DESC", nativeQuery = true)
    List<PlayerStats> findTopPlayersByWins(int minGames);
    
    // Position-based stats queries
    @Query(value = "SELECT * FROM foosball.position_stats ORDER BY total_goals DESC", nativeQuery = true)
    List<PositionStats> findAllPositionStatsOrderedByTotalGoals();
    
    @Query(value = "SELECT * FROM foosball.position_stats ORDER BY total_goalie_goals DESC", nativeQuery = true)
    List<PositionStats> findAllPositionStatsOrderedByGoalieGoals();
    
    @Query(value = "SELECT * FROM foosball.position_stats ORDER BY total_forward_goals DESC", nativeQuery = true)
    List<PositionStats> findAllPositionStatsOrderedByForwardGoals();
    
    @Query(value = "SELECT * FROM foosball.position_stats WHERE total_games >= :minGames ORDER BY total_goals DESC", nativeQuery = true)
    List<PositionStats> findTopScorersByTotalGoals(int minGames);
    
    @Query(value = "SELECT * FROM foosball.position_stats WHERE total_games >= :minGames ORDER BY total_goalie_goals DESC", nativeQuery = true)
    List<PositionStats> findTopGoalieScorers(int minGames);
    
    @Query(value = "SELECT * FROM foosball.position_stats WHERE total_games >= :minGames ORDER BY total_forward_goals DESC", nativeQuery = true)
    List<PositionStats> findTopForwardScorers(int minGames);
    
    // Team performance queries
    @Query(value = "SELECT * FROM foosball.team_stats ORDER BY win_percentage DESC", nativeQuery = true)
    List<TeamStats> findAllTeamStatsOrderedByWinPercentage();
    
    @Query(value = "SELECT * FROM foosball.team_stats ORDER BY games_played_together DESC", nativeQuery = true)
    List<TeamStats> findAllTeamStatsOrderedByGamesPlayed();
    
    @Query(value = "SELECT * FROM foosball.team_stats WHERE games_played_together >= :minGames ORDER BY win_percentage DESC", nativeQuery = true)
    List<TeamStats> findTopTeamsByWinPercentage(int minGames);
    
    @Query(value = "SELECT * FROM foosball.team_stats WHERE games_played_together >= :minGames ORDER BY avg_team_score DESC", nativeQuery = true)
    List<TeamStats> findTopTeamsByAverageScore(int minGames);
}
