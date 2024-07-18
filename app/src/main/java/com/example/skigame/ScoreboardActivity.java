package com.example.skigame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skigame.Managers.ScoreBoardManager;
import com.example.skigame.Models.ScoreboardEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ScoreboardActivity";
    private ScoreBoardManager scoreBoardManager;
    private ListView leaderboardListView;
    private GoogleMap mMap;
    private List<ScoreboardEntry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        try {
            scoreBoardManager = new ScoreBoardManager();
            leaderboardListView = findViewById(R.id.scoreboard_list);

            scoreBoardManager.loadEntries(this);
            entries = scoreBoardManager.getEntries();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }else {
                Log.e(TAG, "Error: Map fragment is null");
                Toast.makeText(this, "Error loading map", Toast.LENGTH_SHORT).show();
            }
            updateScoreBoardList();
        }catch(Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing scoreboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateScoreBoardList() {

        try {
            List<String> displayList = new ArrayList<>();
            for (ScoreboardEntry entry : entries) {
                displayList.add(entry.getName() + " - " + entry.getTotalScore());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, displayList);
            leaderboardListView.setAdapter(adapter);

            leaderboardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showLocationOnMap(entries.get(position));
                }
            });
        }catch (Exception e){
            Log.e(TAG, "Error updating scoreboard list: ", e);
            Toast.makeText(this, "Error updating scoreboard list", Toast.LENGTH_SHORT).show();
        }

    }

    private void showLocationOnMap(ScoreboardEntry scoreboardEntry) {
        if (mMap != null) {
            try {
                LatLng location = new LatLng(scoreboardEntry.getLatitude(), scoreboardEntry.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(scoreboardEntry.getName())
                        .snippet("Score: " + scoreboardEntry.getTotalScore()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
            } catch (Exception e) {
                Log.e(TAG, "Error showing location on map: ", e);
                Toast.makeText(this, "Error displaying location", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Error: Map is null");
            Toast.makeText(this, "Map not initialized", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        try {
            for (ScoreboardEntry entry : entries) {
                LatLng position = new LatLng(entry.getLatitude(), entry.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(entry.getName())
                        .snippet("Score: " + entry.getTotalScore()));
            }
            if (!entries.isEmpty()) {
                ScoreboardEntry firstEntry = entries.get(0);
                LatLng firstPosition = new LatLng(firstEntry.getLatitude(), firstEntry.getLongitude());
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(firstPosition, 10));
            }
        }catch(Exception e){
            Log.e(TAG, "Error in onMapReady: ", e);
            Toast.makeText(this, "Error initializing map", Toast.LENGTH_SHORT).show();
        }
    }

    private void sortEntriesByScore() {
        entries.sort((e1, e2) -> Integer.compare(e2.getTotalScore(), e1.getTotalScore()));
        updateScoreBoardList();
    }

    private void sortEntriesByName() {
        entries.sort((e1, e2) -> e1.getName().compareTo(e2.getName()));
        updateScoreBoardList();
    }

}



