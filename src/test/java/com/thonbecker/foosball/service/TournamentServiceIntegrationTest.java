package com.thonbecker.foosball.service;

import static org.assertj.core.api.Assertions.*;

import com.thonbecker.foosball.AbstractIntegrationTest;
import com.thonbecker.foosball.entity.*;
import com.thonbecker.foosball.model.CreateTournamentRequest;
import com.thonbecker.foosball.repository.*;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=none"})
@Transactional
class TournamentServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentRegistrationRepository registrationRepository;

    @Autowired
    private TournamentMatchRepository matchRepository;

    @Autowired
    private TournamentStandingRepository standingRepository;

    @Autowired
    private EntityManager entityManager;

    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player player5;
    private Player player6;
    private Player player7;
    private Player player8;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        // Clean up
        standingRepository.deleteAll();
        matchRepository.deleteAll();
        registrationRepository.deleteAll();
        tournamentRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();

        // Create players (8 players for 4 teams)
        player1 = playerRepository.save(new Player("Alice", "alice@example.com"));
        player2 = playerRepository.save(new Player("Bob", "bob@example.com"));
        player3 = playerRepository.save(new Player("Charlie", "charlie@example.com"));
        player4 = playerRepository.save(new Player("Diana", "diana@example.com"));
        player5 = playerRepository.save(new Player("Eve", "eve@example.com"));
        player6 = playerRepository.save(new Player("Frank", "frank@example.com"));
        player7 = playerRepository.save(new Player("Grace", "grace@example.com"));
        player8 = playerRepository.save(new Player("Henry", "henry@example.com"));
    }

    @Test
    void shouldCreateTournamentSuccessfully() {
        // Given
        var request = new CreateTournamentRequest(
                "Summer Championship",
                "Annual summer tournament",
                Tournament.TournamentType.SINGLE_ELIMINATION,
                8,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(10),
                null);

        // When
        var created = tournamentService.createTournament(request, player1.getId());

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Summer Championship");
        assertThat(created.getStatus()).isEqualTo(Tournament.TournamentStatus.DRAFT);
        assertThat(created.getCreatedBy()).isEqualTo(player1);
    }

    @Test
    void shouldUpdateStandingsWhenMatchIsCompleted() {
        // Given - Create and start tournament
        tournament = createAndStartTournament();

        var matches = matchRepository.findByTournamentIdOrderByRoundNumberAscMatchNumberAsc(
                tournament.getId());
        var firstMatch = matches.stream()
                .filter(m -> m.getRoundNumber() == 1)
                .filter(m -> m.getStatus() == TournamentMatch.MatchStatus.READY)
                .findFirst()
                .orElseThrow();

        // Create a game result
        var game = createGame(
                firstMatch.getTeam1().getPlayer(),
                firstMatch.getTeam1().getPartner(),
                firstMatch.getTeam2().getPlayer(),
                firstMatch.getTeam2().getPartner(),
                10,
                5);

        // When - Complete the match
        var completedMatch = tournamentService.completeMatch(firstMatch.getId(), game.getId());

        // Then - Verify match is completed
        assertThat(completedMatch.isCompleted()).isTrue();
        assertThat(completedMatch.getWinner()).isNotNull();

        // Verify standings were created and updated
        var standings = standingRepository.findByTournamentIdOrderByPointsDesc(tournament.getId());
        assertThat(standings).hasSize(2);

        var winnerStanding = standings.stream()
                .filter(s -> s.getRegistration().equals(completedMatch.getWinner()))
                .findFirst()
                .orElseThrow();

        var loserStanding = standings.stream()
                .filter(s -> !s.getRegistration().equals(completedMatch.getWinner()))
                .findFirst()
                .orElseThrow();

        // Verify winner stats
        assertThat(winnerStanding.getWins()).isEqualTo(1);
        assertThat(winnerStanding.getLosses()).isEqualTo(0);
        assertThat(winnerStanding.getGamesPlayed()).isEqualTo(1);
        assertThat(winnerStanding.getGoalsFor()).isEqualTo(10);
        assertThat(winnerStanding.getGoalsAgainst()).isEqualTo(5);
        assertThat(winnerStanding.getGoalDifference()).isEqualTo(5);
        assertThat(winnerStanding.getPoints().intValue()).isEqualTo(3); // Default points for win

        // Verify loser stats
        assertThat(loserStanding.getWins()).isEqualTo(0);
        assertThat(loserStanding.getLosses()).isEqualTo(1);
        assertThat(loserStanding.getGamesPlayed()).isEqualTo(1);
        assertThat(loserStanding.getGoalsFor()).isEqualTo(5);
        assertThat(loserStanding.getGoalsAgainst()).isEqualTo(10);
        assertThat(loserStanding.getGoalDifference()).isEqualTo(-5);
        assertThat(loserStanding.getPoints().intValue()).isEqualTo(0); // Default points for loss

        // Verify positions
        assertThat(winnerStanding.getPosition()).isEqualTo(1);
        assertThat(loserStanding.getPosition()).isEqualTo(2);
    }

    @Test
    void shouldUpdateStandingsForMultipleMatches() {
        // Given - Create and start tournament
        tournament = createAndStartTournament();

        var matches = matchRepository.findByTournamentIdOrderByRoundNumberAscMatchNumberAsc(
                tournament.getId());
        var firstRoundMatches = matches.stream()
                .filter(m -> m.getRoundNumber() == 1)
                .filter(m -> m.getStatus() == TournamentMatch.MatchStatus.READY)
                .toList();

        assertThat(firstRoundMatches).hasSize(2);

        // Complete first match
        var match1 = firstRoundMatches.get(0);
        var game1 = createGame(
                match1.getTeam1().getPlayer(),
                match1.getTeam1().getPartner(),
                match1.getTeam2().getPlayer(),
                match1.getTeam2().getPartner(),
                10,
                3);
        tournamentService.completeMatch(match1.getId(), game1.getId());

        // Complete second match
        var match2 = firstRoundMatches.get(1);
        var game2 = createGame(
                match2.getTeam1().getPlayer(),
                match2.getTeam1().getPartner(),
                match2.getTeam2().getPlayer(),
                match2.getTeam2().getPartner(),
                8,
                7);
        tournamentService.completeMatch(match2.getId(), game2.getId());

        // Then - Verify standings for all 4 players
        var standings = standingRepository.findByTournamentIdOrderByPointsDesc(tournament.getId());
        assertThat(standings).hasSize(4);

        // All standings should have exactly 1 game played
        standings.forEach(standing -> {
            assertThat(standing.getGamesPlayed()).isEqualTo(1);
            assertThat(standing.getWins() + standing.getLosses()).isEqualTo(1);
        });

        // 2 winners with 3 points each
        var winners = standings.stream().filter(s -> s.getWins() == 1).toList();
        assertThat(winners).hasSize(2);
        winners.forEach(winner -> assertThat(winner.getPoints().intValue()).isEqualTo(3));

        // 2 losers with 0 points each
        var losers = standings.stream().filter(s -> s.getLosses() == 1).toList();
        assertThat(losers).hasSize(2);
        losers.forEach(loser -> assertThat(loser.getPoints().intValue()).isEqualTo(0));
    }

    @Test
    void shouldAdvanceWinnerAndUpdateStandings() {
        // Given - Create and start tournament
        tournament = createAndStartTournament();

        // Complete first round
        var round1Matches =
                matchRepository
                        .findByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournament.getId())
                        .stream()
                        .filter(m -> m.getRoundNumber() == 1)
                        .filter(m -> m.getStatus() == TournamentMatch.MatchStatus.READY)
                        .toList();

        var semifinal1 = round1Matches.get(0);
        var game1 = createGame(
                semifinal1.getTeam1().getPlayer(),
                semifinal1.getTeam1().getPartner(),
                semifinal1.getTeam2().getPlayer(),
                semifinal1.getTeam2().getPartner(),
                10,
                5);
        tournamentService.completeMatch(semifinal1.getId(), game1.getId());

        var semifinal2 = round1Matches.get(1);
        var game2 = createGame(
                semifinal2.getTeam1().getPlayer(),
                semifinal2.getTeam1().getPartner(),
                semifinal2.getTeam2().getPlayer(),
                semifinal2.getTeam2().getPartner(),
                10,
                6);
        tournamentService.completeMatch(semifinal2.getId(), game2.getId());

        // When - Complete the final
        var finalMatch =
                matchRepository
                        .findByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournament.getId())
                        .stream()
                        .filter(m -> m.getRoundNumber() == 2)
                        .findFirst()
                        .orElseThrow();

        assertThat(finalMatch.getStatus()).isEqualTo(TournamentMatch.MatchStatus.READY);

        var finalGame = createGame(
                finalMatch.getTeam1().getPlayer(),
                finalMatch.getTeam1().getPartner(),
                finalMatch.getTeam2().getPlayer(),
                finalMatch.getTeam2().getPartner(),
                10,
                8);
        tournamentService.completeMatch(finalMatch.getId(), finalGame.getId());

        // Then - Verify tournament is completed
        var completedTournament =
                tournamentRepository.findById(tournament.getId()).orElseThrow();
        assertThat(completedTournament.getStatus())
                .isEqualTo(Tournament.TournamentStatus.COMPLETED);

        // Verify standings
        var standings = standingRepository.findByTournamentIdOrderByPointsDesc(tournament.getId());
        assertThat(standings).hasSize(4);

        // Champion should have 2 wins
        var champion = standings.get(0);
        assertThat(champion.getWins()).isEqualTo(2);
        assertThat(champion.getLosses()).isEqualTo(0);
        assertThat(champion.getPoints().intValue()).isEqualTo(6);
        assertThat(champion.getPosition()).isEqualTo(1);

        // Runner-up should have 1 win, 1 loss
        var runnerUp = standings.get(1);
        assertThat(runnerUp.getWins()).isEqualTo(1);
        assertThat(runnerUp.getLosses()).isEqualTo(1);
        assertThat(runnerUp.getPoints().intValue()).isEqualTo(3);
        assertThat(runnerUp.getPosition()).isEqualTo(2);

        // Semi-finalists should have 0 wins, 1 loss
        var semiFinalists = standings.stream()
                .filter(s -> s.getWins() == 0 && s.getLosses() == 1)
                .toList();
        assertThat(semiFinalists).hasSize(2);
    }

    // Helper methods

    private Tournament createAndStartTournament() {
        var request = new CreateTournamentRequest(
                "Test Tournament",
                "Test Description",
                Tournament.TournamentType.SINGLE_ELIMINATION,
                4,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null);

        var created = tournamentService.createTournament(request, player1.getId());
        tournamentService.openRegistration(created.getId());

        // Register 4 teams (2 players per team)
        createTeamRegistration(created, player1, player2);
        createTeamRegistration(created, player3, player4);
        createTeamRegistration(created, player5, player6);
        createTeamRegistration(created, player7, player8);

        entityManager.clear(); // Clear cache to ensure fresh load

        tournamentService.closeRegistration(created.getId());
        return tournamentService.startTournament(created.getId());
    }

    private TournamentRegistration createTeamRegistration(
            Tournament tournament, Player player, Player partner) {
        var registration = new TournamentRegistration(tournament, player, partner);
        var saved = registrationRepository.save(registration);
        entityManager.flush();
        return saved;
    }

    private Game createGame(
            Player white1,
            Player white2,
            Player black1,
            Player black2,
            int whiteScore,
            int blackScore) {
        var game = new Game();
        game.setWhiteTeamPlayer1(white1);
        game.setWhiteTeamPlayer2(white2);
        game.setBlackTeamPlayer1(black1);
        game.setBlackTeamPlayer2(black2);
        game.setWhiteTeamScore(whiteScore);
        game.setBlackTeamScore(blackScore);

        if (whiteScore > blackScore) {
            game.setWinner(Game.TeamColor.WHITE);
        } else if (blackScore > whiteScore) {
            game.setWinner(Game.TeamColor.BLACK);
        }

        return gameRepository.save(game);
    }
}
