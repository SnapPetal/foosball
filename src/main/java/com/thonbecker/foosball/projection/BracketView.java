package com.thonbecker.foosball.projection;

import com.thonbecker.foosball.entity.TournamentMatch;

import java.time.LocalDateTime;

/**
 * Projection for tournament bracket information
 */
public interface BracketView {
    Long getMatchId();

    Integer getRoundNumber();

    Integer getMatchNumber();

    TournamentMatch.BracketType getBracketType();

    String getTeam1DisplayName();

    String getTeam2DisplayName();

    String getWinnerDisplayName();

    TournamentMatch.MatchStatus getStatus();

    LocalDateTime getScheduledTime();

    LocalDateTime getCompletedAt();

    Long getNextMatchId();

    Long getConsolationMatchId();
}
