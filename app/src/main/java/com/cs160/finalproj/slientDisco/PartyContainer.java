package com.cs160.finalproj.slientDisco;

public class PartyContainer {

    private String partyName;
    private int numPeople;
    private String genre;
    private String songName;

    public PartyContainer(String partyName, int numPeople, String genre, String songName) {
        this.partyName = partyName;
        this.numPeople = numPeople;
        this.genre = genre;
        this.songName = songName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getPartyName() {
        return partyName;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public String getGenre() {
        return genre;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
