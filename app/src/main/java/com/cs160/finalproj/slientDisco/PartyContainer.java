package com.cs160.finalproj.slientDisco;

import android.provider.Telephony;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

public class PartyContainer {

    private String partyName;
    private int numPeople;
    private String genre;
    private String songUri;
    private int distance;
    private ArrayList<String> audience;
    private String code;

    public PartyContainer(String partyName, int numPeople, String genre, String songUri) {
        this.partyName = partyName;
        this.numPeople = numPeople;
        this.genre = genre;
        this.songUri = songUri;
    }


    public String getSongUri() {
        return songUri;
    }

    public void setSongUri(String songUri) {
        this.songUri = songUri;
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

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public ArrayList<String> getAudience() {
        return this.audience;
    }

    public void setAudience(ArrayList<String> audience) {
        this.audience = audience;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
