package com.thonbecker.foosball.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "players", schema = "foosball")
@EntityListeners(AuditingEntityListener.class)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Player name is required")
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Email(message = "Email should be valid")
    @Column(name = "email", length = 255)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "whiteTeamPlayer1", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Game> whiteTeamPlayer1Games = new ArrayList<>();

    @OneToMany(mappedBy = "whiteTeamPlayer2", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Game> whiteTeamPlayer2Games = new ArrayList<>();

    @OneToMany(mappedBy = "blackTeamPlayer1", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Game> blackTeamPlayer1Games = new ArrayList<>();

    @OneToMany(mappedBy = "blackTeamPlayer2", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Game> blackTeamPlayer2Games = new ArrayList<>();

    // Constructors
    public Player() {}

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Game> getWhiteTeamPlayer1Games() {
        return whiteTeamPlayer1Games;
    }

    public void setWhiteTeamPlayer1Games(List<Game> whiteTeamPlayer1Games) {
        this.whiteTeamPlayer1Games = whiteTeamPlayer1Games;
    }

    public List<Game> getWhiteTeamPlayer2Games() {
        return whiteTeamPlayer2Games;
    }

    public void setWhiteTeamPlayer2Games(List<Game> whiteTeamPlayer2Games) {
        this.whiteTeamPlayer2Games = whiteTeamPlayer2Games;
    }

    public List<Game> getBlackTeamPlayer1Games() {
        return blackTeamPlayer1Games;
    }

    public void setBlackTeamPlayer1Games(List<Game> blackTeamPlayer1Games) {
        this.blackTeamPlayer1Games = blackTeamPlayer1Games;
    }

    public List<Game> getBlackTeamPlayer2Games() {
        return blackTeamPlayer2Games;
    }

    public void setBlackTeamPlayer2Games(List<Game> blackTeamPlayer2Games) {
        this.blackTeamPlayer2Games = blackTeamPlayer2Games;
    }

    @Override
    public String toString() {
        return "Player{" + "id="
                + id + ", name='"
                + name + '\'' + ", email='"
                + email + '\'' + ", createdAt="
                + createdAt + '}';
    }
}
