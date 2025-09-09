package com.thonbecker.foosball.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "games", schema = "foosball")
@EntityListeners(AuditingEntityListener.class)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "White team player 1 is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "white_team_player1_id", nullable = false)
    @JsonBackReference
    private Player whiteTeamPlayer1;

    @NotNull(message = "White team player 2 is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "white_team_player2_id", nullable = false)
    @JsonBackReference
    private Player whiteTeamPlayer2;

    @NotNull(message = "Black team player 1 is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "black_team_player1_id", nullable = false)
    @JsonBackReference
    private Player blackTeamPlayer1;

    @NotNull(message = "Black team player 2 is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "black_team_player2_id", nullable = false)
    @JsonBackReference
    private Player blackTeamPlayer2;

    @Min(value = 0, message = "White team score cannot be negative")
    @Column(name = "white_team_score", nullable = false)
    private Integer whiteTeamScore = 0;

    @Min(value = 0, message = "Black team score cannot be negative")
    @Column(name = "black_team_score", nullable = false)
    private Integer blackTeamScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "winner", length = 10)
    private TeamColor winner;

    @CreatedDate
    @Column(name = "played_at", nullable = false, updatable = false)
    private LocalDateTime playedAt;

    @Column(name = "notes", length = 500)
    private String notes;

    public enum TeamColor {
        WHITE,
        BLACK
    }

    // Constructors
    public Game() {}

    public Game(Player whiteTeamPlayer1, Player whiteTeamPlayer2, Player blackTeamPlayer1, Player blackTeamPlayer2) {
        this.whiteTeamPlayer1 = whiteTeamPlayer1;
        this.whiteTeamPlayer2 = whiteTeamPlayer2;
        this.blackTeamPlayer1 = blackTeamPlayer1;
        this.blackTeamPlayer2 = blackTeamPlayer2;
    }

    // Business logic methods
    public void setScores(int whiteTeamScore, int blackTeamScore) {
        this.whiteTeamScore = whiteTeamScore;
        this.blackTeamScore = blackTeamScore;
        determineWinner();
    }

    private void determineWinner() {
        if (whiteTeamScore > blackTeamScore) {
            this.winner = TeamColor.WHITE;
        } else if (blackTeamScore > whiteTeamScore) {
            this.winner = TeamColor.BLACK;
        } else {
            this.winner = null; // Draw
        }
    }

    public boolean isDraw() {
        return winner == null;
    }

    public boolean isWhiteTeamWinner() {
        return TeamColor.WHITE.equals(winner);
    }

    public boolean isBlackTeamWinner() {
        return TeamColor.BLACK.equals(winner);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getWhiteTeamPlayer1() {
        return whiteTeamPlayer1;
    }

    public void setWhiteTeamPlayer1(Player whiteTeamPlayer1) {
        this.whiteTeamPlayer1 = whiteTeamPlayer1;
    }

    public Player getWhiteTeamPlayer2() {
        return whiteTeamPlayer2;
    }

    public void setWhiteTeamPlayer2(Player whiteTeamPlayer2) {
        this.whiteTeamPlayer2 = whiteTeamPlayer2;
    }

    public Player getBlackTeamPlayer1() {
        return blackTeamPlayer1;
    }

    public void setBlackTeamPlayer1(Player blackTeamPlayer1) {
        this.blackTeamPlayer1 = blackTeamPlayer1;
    }

    public Player getBlackTeamPlayer2() {
        return blackTeamPlayer2;
    }

    public void setBlackTeamPlayer2(Player blackTeamPlayer2) {
        this.blackTeamPlayer2 = blackTeamPlayer2;
    }

    public Integer getWhiteTeamScore() {
        return whiteTeamScore;
    }

    public void setWhiteTeamScore(Integer whiteTeamScore) {
        this.whiteTeamScore = whiteTeamScore;
    }

    public Integer getBlackTeamScore() {
        return blackTeamScore;
    }

    public void setBlackTeamScore(Integer blackTeamScore) {
        this.blackTeamScore = blackTeamScore;
    }

    public TeamColor getWinner() {
        return winner;
    }

    public void setWinner(TeamColor winner) {
        this.winner = winner;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Game{" + "id="
                + id + ", whiteTeamPlayer1="
                + (whiteTeamPlayer1 != null ? whiteTeamPlayer1.getName() : "null") + ", whiteTeamPlayer2="
                + (whiteTeamPlayer2 != null ? whiteTeamPlayer2.getName() : "null") + ", blackTeamPlayer1="
                + (blackTeamPlayer1 != null ? blackTeamPlayer1.getName() : "null") + ", blackTeamPlayer2="
                + (blackTeamPlayer2 != null ? blackTeamPlayer2.getName() : "null") + ", whiteTeamScore="
                + whiteTeamScore + ", blackTeamScore="
                + blackTeamScore + ", winner="
                + winner + ", playedAt="
                + playedAt + '}';
    }
}
