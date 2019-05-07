package com.cs160.finalproj.slientDisco;

import android.provider.Telephony;

import java.util.Comparator;

public class PartyContainer {

    private String partyName;
    private int numPeople;
    private String genre;
    private String songName;
    private int distance;

    public PartyContainer(String partyName, int numPeople, String genre, String songName, int distance) {
        this.partyName = partyName;
        this.numPeople = numPeople;
        this.genre = genre;
        this.songName = songName;
        this.distance = distance;
    }

    public String songToString() {
        return this.songName;
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

    public int getDistance() {
        return distance;
    }
}

class SortbyPartyName implements Comparator<PartyContainer> {

    public int compare(PartyContainer a, PartyContainer b) {
        return a.getPartyName().compareTo(b.getPartyName());
    }
}

class SortbyNumPeople implements Comparator<PartyContainer> {
    public int compare(PartyContainer a, PartyContainer b) {
        return b.getNumPeople() - a.getNumPeople();
    }
}

class SortbyDistance implements Comparator<PartyContainer> {
    public int compare(PartyContainer a, PartyContainer b) {
        return a.getDistance() - b.getDistance();
    }
}
