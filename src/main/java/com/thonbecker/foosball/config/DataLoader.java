package com.thonbecker.foosball.config;

import com.thonbecker.foosball.entity.Player;
import com.thonbecker.foosball.service.FoosballService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

        // Create sample players - expanded roster
        Player alice = getOrCreatePlayer("Alice", "alice@example.com");
        Player bob = getOrCreatePlayer("Bob", "bob@example.com");
        Player charlie = getOrCreatePlayer("Charlie", "charlie@example.com");
        Player diana = getOrCreatePlayer("Diana", "diana@example.com");
        Player eve = getOrCreatePlayer("Eve", "eve@example.com");
        Player frank = getOrCreatePlayer("Frank", "frank@example.com");
        Player grace = getOrCreatePlayer("Grace", "grace@example.com");
        Player henry = getOrCreatePlayer("Henry", "henry@example.com");
        Player iris = getOrCreatePlayer("Iris", "iris@example.com");
        Player jack = getOrCreatePlayer("Jack", "jack@example.com");
        Player kate = getOrCreatePlayer("Kate", "kate@example.com");
        Player liam = getOrCreatePlayer("Liam", "liam@example.com");

        System.out.println("Created " + foosballService.getTotalPlayers() + " players");

        // Check if we already have games to avoid duplicates
        if (foosballService.getTotalGames() > 0) {
            System.out.println("Sample games already exist, skipping game creation.");
            return;
        }

        // Record varied sample games with different outcomes

        // Close competitive games
        foosballService.recordGame(alice, bob, charlie, diana, 5, 4);
        foosballService.recordGame(eve, frank, grace, henry, 5, 3);
        foosballService.recordGame(iris, jack, kate, liam, 5, 5); // Draw

        // Some dominant victories
        foosballService.recordGame(alice, charlie, bob, diana, 5, 1);
        foosballService.recordGame(grace, iris, eve, frank, 5, 0);

        // Mixed matchups
        foosballService.recordGame(bob, henry, alice, kate, 5, 4);
        foosballService.recordGame(charlie, jack, diana, liam, 5, 3);
        foosballService.recordGame(eve, kate, frank, iris, 5, 5); // Draw

        // Upset victories (weaker players beating stronger ones)
        foosballService.recordGame(liam, diana, alice, bob, 5, 4);
        foosballService.recordGame(frank, henry, charlie, grace, 5, 3);

        // High-scoring games
        foosballService.recordGame(alice, iris, eve, jack, 8, 6);
        foosballService.recordGame(bob, kate, henry, liam, 7, 5);

        // Low-scoring defensive games
        foosballService.recordGame(charlie, frank, diana, grace, 3, 1);
        foosballService.recordGame(alice, liam, bob, iris, 2, 1);

        // More draws for variety
        foosballService.recordGame(eve, henry, charlie, kate, 4, 4);
        foosballService.recordGame(frank, jack, diana, iris, 6, 6);

        // Tournament-style progression games
        foosballService.recordGame(alice, charlie, bob, diana, 5, 2);
        foosballService.recordGame(eve, grace, frank, henry, 5, 3);
        foosballService.recordGame(iris, kate, jack, liam, 5, 4);

        // Championship-style games
        foosballService.recordGame(alice, eve, charlie, grace, 5, 4);
        foosballService.recordGame(bob, iris, diana, kate, 5, 3);

        // Final championship
        foosballService.recordGame(alice, bob, eve, iris, 6, 5);

        System.out.println("Sample data loaded successfully!");
        System.out.println("Total players: " + foosballService.getTotalPlayers());
        System.out.println("Total games: " + foosballService.getTotalGames());
    }
}
