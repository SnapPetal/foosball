package com.thonbecker.foosball.model;

import jakarta.validation.constraints.NotNull;

public record MatchResultRequest(
        @NotNull(message = "Game ID is required") Long gameId,
        @NotNull(message = "Winner registration ID is required") Long winnerRegistrationId) {}
