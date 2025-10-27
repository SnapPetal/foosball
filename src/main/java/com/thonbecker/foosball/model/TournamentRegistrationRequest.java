package com.thonbecker.foosball.model;

import jakarta.validation.constraints.NotNull;

public record TournamentRegistrationRequest(
        @NotNull(message = "Player ID is required") Long playerId,
        Long partnerId,
        String teamName) {
    public boolean isTeamRegistration() {
        return partnerId != null;
    }
}
