package com.example.skigame.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.skigame.Models.ScoreboardEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ScoreBoardManager {

    private static final int MAX_ENTRIES = 10;
    private static final String PREFS_NAME = "SkiGamePrefs";
    private static final String TOP_SCORES_KEY = "TopScores";
    private static final Comparator<ScoreboardEntry> SCORE_COMPARATOR = (o1, o2) -> Integer.compare(o2.getTotalScore(), o1.getTotalScore());
    private List<ScoreboardEntry> entries;

    public ScoreBoardManager() {
        entries = new ArrayList<>();
    }

    public synchronized void addEntry(ScoreboardEntry entry){
        entries.add(entry);
        sortEntries();
        if(entries.size() > MAX_ENTRIES)
            entries.remove(entries.size()-1);
    }

    private void sortEntries() {
        Collections.sort(entries, SCORE_COMPARATOR);
    }

    public void saveEntries(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(entries);
        editor.putString(TOP_SCORES_KEY, json);
        editor.apply();
    }

    public void loadEntries(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(TOP_SCORES_KEY, null);

        if (json != null){
            try{
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<ScoreboardEntry>>() {}.getType();
                entries = gson.fromJson(json, type);
            } catch (Exception e){
                Log.d("ScoreBoardManager", "Error Loading Entries", e);
                entries = new ArrayList<>();
            }
        }

        if (entries == null) {
            entries = new ArrayList<>();
        }
    }

    public List<ScoreboardEntry> getEntries() {
        return new ArrayList<>(entries);}

}
