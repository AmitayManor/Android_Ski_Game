package com.example.skigame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity{

    private static final String GAME_SPEED = "GAME_SPEED";
    private static final int SLOW_GAME = 0;
    private static final int FAST_GAME = 1;


@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);

    Button scoreBoardButton = findViewById(R.id.leaderboard_button);
    Button startGameButton = findViewById(R.id.start_game_button);
    Button startTiltGameButton = findViewById(R.id.start_tilt_game_button);
    RadioGroup speedGroup = findViewById(R.id.pace_group);

    startGameButton.setOnClickListener(v -> {
        int selectedPace = (speedGroup.getCheckedRadioButtonId() == R.id.radio_fast) ? FAST_GAME : SLOW_GAME;
        navigateToActivity(MainActivity.class, GAME_SPEED, selectedPace);
    });

    startTiltGameButton.setOnClickListener(v -> navigateToActivity(TiltcontrolActivity.class));

    scoreBoardButton.setOnClickListener(v -> navigateToActivity(ScoreboardActivity.class));
}

    private void navigateToActivity(Class<?> activityClass) {
        navigateToActivity(activityClass, null, 0);
    }

    private void navigateToActivity(Class<?> activityClass, String extraKey, int extraValue) {
        Intent intent = new Intent(MenuActivity.this, activityClass);
        if (extraKey != null) {
            intent.putExtra(extraKey, extraValue);
        }
        startActivity(intent);
    }

    private void starSensorGame() {
        Intent intent = new Intent(MenuActivity.this, TiltcontrolActivity.class);
        startActivity(intent);
    }

    private void openScoreboard() {
        Intent intent = new Intent(MenuActivity.this, ScoreboardActivity.class);
        startActivity(intent);
    }

    private void startGame(int selectedPace) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra(GAME_SPEED, selectedPace);
        startActivity(intent);
    }

}
