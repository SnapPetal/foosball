package com.thonbecker.foosball.config;

import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.service.FoosballService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DataLoader {

    private final FoosballService foosballService;

    @Value("${foosball.sample-data.enabled:true}")
    private boolean sampleDataEnabled;

    @Autowired
    public DataLoader(FoosballService foosballService) {
        this.foosballService = foosballService;
    }

    /**
     * Helper method to get an existing player or create a new one
     */
    private Player getOrCreatePlayer(String name, String email) {
        // First try to find existing player
        Optional<Player> existingPlayer = foosballService.findPlayerByName(name);
        if (existingPlayer.isPresent()) {
            System.out.println("Player " + name + " already exists, using existing player.");
            return existingPlayer.get();
        }

        // If player doesn't exist, create new one
        System.out.println("Creating new player: " + name);
        return foosballService.createPlayer(name, email);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run(ApplicationReadyEvent event) throws Exception {
        if (!sampleDataEnabled) {
            return;
        }

        System.out.println("Loading sample foosball data...");

        // Create sample players
        Player alice = getOrCreatePlayer("Alice", "alice@example.com");
        Player bob = getOrCreatePlayer("Bob", "bob@example.com");
        Player charlie = getOrCreatePlayer("Charlie", "charlie@example.com");
        Player diana = getOrCreatePlayer("Diana", "diana@example.com");
        Player eve = getOrCreatePlayer("Eve", "eve@example.com");
        Player frank = getOrCreatePlayer("Frank", "frank@example.com");

        System.out.println("Created players: " + alice.getName() + ", " + bob.getName() + ", " + charlie.getName()
                + ", " + diana.getName() + ", " + eve.getName() + ", " + frank.getName());

        // Check if we already have games to avoid duplicates
        if (foosballService.getTotalGames() > 0) {
            System.out.println("Sample games already exist, skipping game creation.");
            return;
        }

        // Record some sample games with position-based scoring
        foosballService.recordGameWithPositionScores(
                alice,
                bob,
                charlie,
                diana, // White team: Alice (goalie), Bob (forward)
                3,
                2,
                1,
                4, // White: 3 goalie + 2 forward = 5 total, Black: 1 goalie + 4 forward = 5 total
                15,
                "Great game, very close!");

        foosballService.recordGameWithPositionScores(
                eve,
                frank,
                alice,
                bob, // White team: Eve (goalie), Frank (forward)
                2,
                3,
                1,
                2, // White: 2 goalie + 3 forward = 5 total, Black: 1 goalie + 2 forward = 3 total
                12,
                "Eve and Frank dominated!");

        foosballService.recordGameWithPositionScores(
                charlie,
                diana,
                eve,
                frank, // White team: Charlie (goalie), Diana (forward)
                4,
                1,
                2,
                3, // White: 4 goalie + 1 forward = 5 total, Black: 2 goalie + 3 forward = 5 total
                18,
                "Another close match!");

        foosballService.recordGameWithPositionScores(
                bob,
                alice,
                charlie,
                diana, // White team: Bob (goalie), Alice (forward)
                1,
                4,
                2,
                2, // White: 1 goalie + 4 forward = 5 total, Black: 2 goalie + 2 forward = 4 total
                14,
                "Bob and Alice make a great team!");

        foosballService.recordGameWithPositionScores(
                frank,
                eve,
                bob,
                alice, // White team: Frank (goalie), Eve (forward)
                3,
                2,
                1,
                3, // White: 3 goalie + 2 forward = 5 total, Black: 1 goalie + 3 forward = 4 total
                16,
                "Frank's goalie skills are improving!");

        System.out.println("Sample data loaded successfully!");
        System.out.println("Total players: " + foosballService.getTotalPlayers());
        System.out.println("Total games: " + foosballService.getTotalGames());
    }
}
