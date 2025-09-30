package com.thonbecker.foosball.model;

import jakarta.validation.constraints.NotNull;

public record WalkoverRequest(
        @NotNull(message = "Winner registration ID is required") Long winnerRegistrationId,
        String reason) {}
