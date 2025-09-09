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

        // Record some sample games
        foosballService.recordGame(
                alice, bob, charlie, diana, // White team: Alice, Bob
                5, 5); // Draw

        foosballService.recordGame(
                eve, frank, alice, bob, // White team: Eve, Frank
                5, 3);

        foosballService.recordGame(
                charlie, diana, eve, frank, // White team: Charlie, Diana
                5, 5); // Draw

        foosballService.recordGame(
                bob, alice, charlie, diana, // White team: Bob, Alice
                5, 4);

        foosballService.recordGame(
                frank, eve, bob, alice, // White team: Frank, Eve
                5, 4);

        System.out.println("Sample data loaded successfully!");
        System.out.println("Total players: " + foosballService.getTotalPlayers());
        System.out.println("Total games: " + foosballService.getTotalGames());
    }
}
