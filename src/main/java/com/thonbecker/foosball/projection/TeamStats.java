package com.thonbecker.foosball.projection;

import org.springframework.beans.factory.annotation.Value;

public interface TeamStats {

    @Value("#{target.player1_id}")
    Long getPlayer1Id();
    
    @Value("#{target.player1_name}")
    String getPlayer1Name();
    
    @Value("#{target.player2_id}")
    Long getPlayer2Id();
    
    @Value("#{target.player2_name}")
    String getPlayer2Name();
    
    @Value("#{target.games_played_together}")
    Long getGamesPlayedTogether();
    
    @Value("#{target.wins_together}")
    Long getWinsTogether();
    
    @Value("#{target.avg_team_score}")
    Double getAverageTeamScore();
    
    default Long getLossesTogether() {
        Long games = getGamesPlayedTogether();
        Long wins = getWinsTogether();
        return games != null && wins != null ? games - wins : 0L;
    }
    
    default Double getWinPercentage() {
        Long games = getGamesPlayedTogether();
        Long wins = getWinsTogether();
        if (games == null || wins == null || games == 0) return 0.0;
        return (double) wins / games * 100.0;
    }
    
    default String getTeamName() {
        String player1 = getPlayer1Name();
        String player2 = getPlayer2Name();
        if (player1 == null || player2 == null) return "Unknown Team";
        return player1 + " & " + player2;
    }
    
    default String getFormattedWinPercentage() {
        Double percentage = getWinPercentage();
        return String.format("%.1f%%", percentage);
    }
    
    default String getPerformanceRating() {
        Double winPercentage = getWinPercentage();
        if (winPercentage == null) return "Unknown";
        
        if (winPercentage >= 80.0) return "Elite";
        else if (winPercentage >= 65.0) return "Strong";
        else if (winPercentage >= 50.0) return "Competitive";
        else if (winPercentage >= 35.0) return "Developing";
        else return "Learning";
    }
}
