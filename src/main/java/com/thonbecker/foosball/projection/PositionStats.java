package com.thonbecker.foosball.projection;

import org.springframework.beans.factory.annotation.Value;

public interface PositionStats {

    Long getId();

    String getName();

    @Value("#{target.total_games}")
    Long getTotalGames();

    @Value("#{target.total_goalie_goals}")
    Long getTotalGoalieGoals();

    @Value("#{target.total_forward_goals}")
    Long getTotalForwardGoals();

    @Value("#{target.total_goals}")
    Long getTotalGoals();

    default Long getGoalsPerGame() {
        Long total = getTotalGoals();
        Long games = getTotalGames();
        return total != null && games != null && games > 0 ? total / games : 0L;
    }

    default String getPreferredPosition() {
        Long goalieGoals = getTotalGoalieGoals();
        Long forwardGoals = getTotalForwardGoals();

        if (goalieGoals == null || forwardGoals == null) return "Unknown";

        if (goalieGoals > forwardGoals) {
            return "Goalie";
        } else if (forwardGoals > goalieGoals) {
            return "Forward";
        } else {
            return "Balanced";
        }
    }

    default Double getGoalieEfficiency() {
        Long goalieGoals = getTotalGoalieGoals();
        Long games = getTotalGames();
        if (goalieGoals == null || games == null || games == 0) return 0.0;
        return (double) goalieGoals / games;
    }

    default Double getForwardEfficiency() {
        Long forwardGoals = getTotalForwardGoals();
        Long games = getTotalGames();
        if (forwardGoals == null || games == null || games == 0) return 0.0;
        return (double) forwardGoals / games;
    }
}
