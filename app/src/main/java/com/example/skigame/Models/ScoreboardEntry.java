package com.example.skigame.Models;

import androidx.annotation.NonNull;

import java.util.Date;

public class ScoreboardEntry {

    private final String name;
    private final int totalScore;
    private final double latitude;
    private final double longitude;
    private final Date timeStamp;

    public ScoreboardEntry(String name, int totalScore, double latitude, double longitude, Date timeStamp){
        this.name = name.trim();
        this. totalScore = totalScore;
        this.latitude=latitude;
        this.longitude=longitude;
        this.timeStamp = new Date(timeStamp.getTime());
    }

    public String getName() {
        return name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getTimeStamp(){ return new Date(timeStamp.getTime());}

    @NonNull
    @Override
    public String toString() {
        return "ScoreboardEntry{" +
                "name='" + name + '\'' +
                ", totalScore=" + totalScore +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timeStamp +
                '}';
    }
}
