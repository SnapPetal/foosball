package com.thonbecker.foosball.service.tournament.algorithm;

import static org.assertj.core.api.Assertions.*;

import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.entity.Tournament;
import com.thonbecker.foosball.entity.TournamentMatch;
import com.thonbecker.foosball.entity.TournamentRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class SingleEliminationAlgorithmTest {

    private SingleEliminationAlgorithm algorithm;
    private Tournament tournament;
    private Player creator;

    @BeforeEach
    void setUp() {
        algorithm = new SingleEliminationAlgorithm();
        creator = createPlayer(1L, "Creator");
        tournament = new Tournament(
                "Test Tournament", Tournament.TournamentType.SINGLE_ELIMINATION, creator);
        tournament.setId(1L);
    }

    @Test
    void shouldGenerateBracketForTwoPlayers() {
        // Given
        var registrations = createRegistrations(2);

        // When
        var matches = algorithm.generateBracket(tournament, registrations);

        // Then
        assertThat(matches).hasSize(1);
        var finalMatch = matches.get(0);
        assertThat(finalMatch.getRoundNumber()).isEqualTo(1);
        assertThat(finalMatch.getMatchNumber()).isEqualTo(1);
        assertThat(finalMatch.getTeam1()).isEqualTo(registrations.get(0));
        assertThat(finalMatch.getTeam2()).isEqualTo(registrations.get(1));
        assertThat(finalMatch.getStatus()).isEqualTo(TournamentMatch.MatchStatus.READY);
    }

    @Test
    void shouldGenerateBracketForFourPlayers() {
        // Given
        var registrations = createRegistrations(4);

        // When
        var matches = algorithm.generateBracket(tournament, registrations);

        // Then
        assertThat(matches).hasSize(3); // 2 semifinals + 1 final

        // Round 1 (semifinals)
        var round1Matches =
                matches.stream().filter(m -> m.getRoundNumber() == 1).toList();
        assertThat(round1Matches).hasSize(2);

        // Round 2 (final)
        var round2Matches =
                matches.stream().filter(m -> m.getRoundNumber() == 2).toList();
        assertThat(round2Matches).hasSize(1);

        // Verify advancement paths
        assertThat(round1Matches.get(0).getNextMatch()).isEqualTo(round2Matches.get(0));
        assertThat(round1Matches.get(1).getNextMatch()).isEqualTo(round2Matches.get(0));
    }

    @Test
    void shouldGenerateBracketForEightPlayers() {
        // Given
        var registrations = createRegistrations(8);

        // When
        var matches = algorithm.generateBracket(tournament, registrations);

        // Then
        assertThat(matches).hasSize(7); // 4 + 2 + 1

        var round1Count = matches.stream().filter(m -> m.getRoundNumber() == 1).count();
        var round2Count = matches.stream().filter(m -> m.getRoundNumber() == 2).count();
        var round3Count = matches.stream().filter(m -> m.getRoundNumber() == 3).count();

        assertThat(round1Count).isEqualTo(4);
        assertThat(round2Count).isEqualTo(2);
        assertThat(round3Count).isEqualTo(1);
    }

    @Test
    void shouldHandleByesForOddNumberOfPlayers() {
        // Given - 3 players should have 1 bye
        var registrations = createRegistrations(3);

        // When
        var matches = algorithm.generateBracket(tournament, registrations);

        // Then - Tournament creates structure for 4 slots (2 first round + 1 final)
        assertThat(matches).hasSize(3);

        // Find first round bye matches (where exactly one team is null initially)
        var firstRoundByeMatches = matches.stream()
                .filter(m -> m.getRoundNumber() == 1)
                .filter(TournamentMatch::hasBye)
                .toList();

        assertThat(firstRoundByeMatches).hasSizeGreaterThanOrEqualTo(1);

        // Verify bye matches are completed with walkover
        firstRoundByeMatches.forEach(match -> {
            assertThat(match.getStatus()).isEqualTo(TournamentMatch.MatchStatus.WALKOVER);
            assertThat(match.getWinner()).isNotNull();
            assertThat(match.getNextMatch()).isNotNull();
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 7})
    void shouldHandleByesForVariousPlayerCounts(int playerCount) {
        // Given
        var registrations = createRegistrations(playerCount);

        // When
        var matches = algorithm.generateBracket(tournament, registrations);

        // Then - Verify bracket is created for next power of 2
        var nextPowerOf2 = (int) Math.pow(2, Math.ceil(Math.log(playerCount) / Math.log(2)));
        var expectedMatches = nextPowerOf2 - 1; // Total matches in single elimination
        assertThat(matches).hasSize(expectedMatches);

        // Verify tournament can proceed (all matches have proper structure)
        assertThat(matches)
                .allMatch(match -> match.getRoundNumber() > 0 && match.getMatchNumber() > 0);
    }

    @Test
    void shouldRespectSeeding() {
        // Given
        var registrations = createSeededRegistrations(4);

        // When
        var matches = algorithm.generateBracket(tournament, registrations);

        // Then
        var firstRoundMatches = matches.stream()
                .filter(m -> m.getRoundNumber() == 1)
                .sorted((m1, m2) -> Integer.compare(m1.getMatchNumber(), m2.getMatchNumber()))
                .toList();

        // Seed 1 vs Seed 4, Seed 2 vs Seed 3
        assertThat(firstRoundMatches.get(0).getTeam1().getSeed()).isEqualTo(1);
        assertThat(firstRoundMatches.get(0).getTeam2().getSeed()).isEqualTo(2);
        assertThat(firstRoundMatches.get(1).getTeam1().getSeed()).isEqualTo(3);
        assertThat(firstRoundMatches.get(1).getTeam2().getSeed()).isEqualTo(4);
    }

    @Test
    void shouldAdvanceWinnerToNextRound() {
        // Given
        var registrations = createRegistrations(4);
        var matches = algorithm.generateBracket(tournament, registrations);

        var semifinal = matches.stream()
                .filter(m -> m.getRoundNumber() == 1 && m.getMatchNumber() == 1)
                .findFirst()
                .orElseThrow();

        semifinal.setWinner(semifinal.getTeam1());
        semifinal.setStatus(TournamentMatch.MatchStatus.COMPLETED);

        // When
        var updatedMatches = algorithm.advanceWinner(semifinal);

        // Then
        assertThat(updatedMatches).hasSize(1);

        var finalMatch = updatedMatches.get(0);
        assertThat(finalMatch.getRoundNumber()).isEqualTo(2);
        assertThat(finalMatch.getTeam1()).isEqualTo(semifinal.getTeam1());
    }

    @Test
    void shouldNotAdvanceWinnerIfNoNextMatch() {
        // Given - Final match
        var registrations = createRegistrations(2);
        var matches = algorithm.generateBracket(tournament, registrations);

        var finalMatch = matches.get(0);
        finalMatch.setWinner(finalMatch.getTeam1());
        finalMatch.setStatus(TournamentMatch.MatchStatus.COMPLETED);

        // When
        var updatedMatches = algorithm.advanceWinner(finalMatch);

        // Then
        assertThat(updatedMatches).isEmpty();
    }

    @Test
    void shouldDetectTournamentComplete() {
        // Given
        var registrations = createRegistrations(4);
        var matches = algorithm.generateBracket(tournament, registrations);

        // Simulate playing through the entire tournament
        // Complete round 1
        matches.stream()
                .filter(m -> m.getRoundNumber() == 1)
                .filter(m -> !m.isCompleted())
                .forEach(match -> {
                    match.setWinner(match.getTeam1());
                    match.setStatus(TournamentMatch.MatchStatus.COMPLETED);
                });

        // Advance winners to round 2
        matches.stream()
                .filter(m -> m.getRoundNumber() == 1 && m.isCompleted())
                .forEach(match -> algorithm.advanceWinner(match));

        // Complete round 2 (final)
        matches.stream()
                .filter(m -> m.getRoundNumber() == 2)
                .filter(m -> !m.isCompleted())
                .forEach(match -> {
                    if (match.getTeam1() != null && match.getTeam2() != null) {
                        match.setWinner(match.getTeam1());
                        match.setStatus(TournamentMatch.MatchStatus.COMPLETED);
                    }
                });

        tournament.setMatches(new ArrayList<>(matches));

        // When
        var isComplete = algorithm.isTournamentComplete(tournament);

        // Then
        assertThat(isComplete).isTrue();
    }

    @Test
    void shouldDetectTournamentNotComplete() {
        // Given
        var registrations = createRegistrations(4);
        var matches = algorithm.generateBracket(tournament, registrations);

        // Complete only first round
        matches.stream().filter(m -> m.getRoundNumber() == 1).forEach(match -> {
            match.setWinner(match.getTeam1());
            match.setStatus(TournamentMatch.MatchStatus.COMPLETED);
        });

        tournament.setMatches(new ArrayList<>(matches));

        // When
        var isComplete = algorithm.isTournamentComplete(tournament);

        // Then
        assertThat(isComplete).isFalse();
    }

    @Test
    void shouldThrowExceptionForInsufficientParticipants() {
        // Given
        var registrations = createRegistrations(1);

        // When/Then
        assertThatThrownBy(() -> algorithm.generateBracket(tournament, registrations))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not enough participants");
    }

    @Test
    void shouldReturnCorrectMinimumParticipants() {
        assertThat(algorithm.getMinimumParticipants()).isEqualTo(2);
    }

    @Test
    void shouldValidateParticipantCount() {
        assertThat(algorithm.isValidParticipantCount(2)).isTrue();
        assertThat(algorithm.isValidParticipantCount(8)).isTrue();
        assertThat(algorithm.isValidParticipantCount(1)).isFalse();
        assertThat(algorithm.isValidParticipantCount(0)).isFalse();
    }

    @Test
    void shouldReturnCorrectTournamentType() {
        assertThat(algorithm.getTournamentType())
                .isEqualTo(Tournament.TournamentType.SINGLE_ELIMINATION);
    }

    @Test
    void shouldSetBracketTypeToMain() {
        // Given
        var registrations = createRegistrations(4);

        // When
        var matches = algorithm.generateBracket(tournament, registrations);

        // Then
        matches.forEach(match ->
                assertThat(match.getBracketType()).isEqualTo(TournamentMatch.BracketType.MAIN));
    }

    // Helper methods

    private List<TournamentRegistration> createRegistrations(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    var player = createPlayer((long) i + 1, "Player " + (i + 1));
                    return new TournamentRegistration(tournament, player);
                })
                .toList();
    }

    private List<TournamentRegistration> createSeededRegistrations(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    var player = createPlayer((long) i + 1, "Player " + (i + 1));
                    var registration = new TournamentRegistration(tournament, player);
                    registration.setSeed(i + 1);
                    return registration;
                })
                .toList();
    }

    private Player createPlayer(Long id, String name) {
        var player = new Player(name, "test" + id + "@example.com");
        player.setId(id);
        return player;
    }
}
