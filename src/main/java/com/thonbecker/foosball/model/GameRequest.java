package com.thonbecker.foosball.model;

public class GameRequest {
    private String whiteTeamPlayer1;
    private String whiteTeamPlayer2;
    private String blackTeamPlayer1;
    private String blackTeamPlayer2;
    private int whiteTeamScore;
    private int blackTeamScore;
    private String notes;

    public String getWhiteTeamPlayer1() {
        return whiteTeamPlayer1;
    }

    public void setWhiteTeamPlayer1(String whiteTeamPlayer1) {
        this.whiteTeamPlayer1 = whiteTeamPlayer1;
    }

    public String getWhiteTeamPlayer2() {
        return whiteTeamPlayer2;
    }

    public void setWhiteTeamPlayer2(String whiteTeamPlayer2) {
        this.whiteTeamPlayer2 = whiteTeamPlayer2;
    }

    public String getBlackTeamPlayer1() {
        return blackTeamPlayer1;
    }

    public void setBlackTeamPlayer1(String blackTeamPlayer1) {
        this.blackTeamPlayer1 = blackTeamPlayer1;
    }

    public String getBlackTeamPlayer2() {
        return blackTeamPlayer2;
    }

    public void setBlackTeamPlayer2(String blackTeamPlayer2) {
        this.blackTeamPlayer2 = blackTeamPlayer2;
    }

    public int getWhiteTeamScore() {
        return whiteTeamScore;
    }

    public void setWhiteTeamScore(int whiteTeamScore) {
        this.whiteTeamScore = whiteTeamScore;
    }

    public int getBlackTeamScore() {
        return blackTeamScore;
    }

    public void setBlackTeamScore(int blackTeamScore) {
        this.blackTeamScore = blackTeamScore;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
