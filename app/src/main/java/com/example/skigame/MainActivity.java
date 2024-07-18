package com.example.skigame;

import android.os.Bundle;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.skigame.Interfaces.CollisionCallback;
import com.example.skigame.Models.ScoreboardEntry;
import com.example.skigame.Utils.SoundPlayer;
import com.example.skigame.Managers.ScoreBoardManager;
import com.example.skigame.Logic.GameManager;
import com.example.skigame.Utils.LocationHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationHandler.LocationUpdateListener, CollisionCallback {

    private static final String TAG = "MainActivity";

    private static final int ROWS = 8;
    private static final int COLS = 5;
    private static final int LIVES =3 ;
    private static final long DELAY = 500;
    private static final int LOCATION_PER_REQ_CODE = 1000;
    private static final String GAME_SPEED = "GAME_SPEED";
    private static final int SLOW_GAME = 0;
    private static final int FAST_GAME = 1;

    protected LocationHandler locationHandler;
    protected double currentLatitude;
    protected double currentLongitude;

    private GameManager gameManager;
    private FrameLayout[] lanes;
    private AppCompatImageView[] hearts;
    private TextView totalScoretextView;
    private boolean timerOn = false;
    private Timer timer;
    private SoundPlayer soundPlayer;
    private ScoreBoardManager scoreBoardManager;
    private FloatingActionButton pauseButton;
    private AlertDialog pauseDialog;
    private boolean isPaused = false;
    private long gameDelay;

    private FusedLocationProviderClient fusedLocationProviderClient;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);
        gameManager = new GameManager(ROWS, COLS, LIVES, soundPlayer);
        scoreBoardManager = new ScoreBoardManager();
        scoreBoardManager.loadEntries(this);
        pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(v -> showPauseMenu());

        int gameSpeed = getIntent().getIntExtra(GAME_SPEED, FAST_GAME);
        gameDelay = (gameSpeed == SLOW_GAME) ? (long)(DELAY * 1.35) : DELAY;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationHandler = new LocationHandler(this);
        locationHandler.setLocationUpdateListener((LocationHandler.LocationUpdateListener) this);

        checkLocationServices();

        initializeViews();

    }


    private void initializeViews() {

        soundPlayer = new SoundPlayer(this);
        gameManager = new GameManager(ROWS,COLS, LIVES, soundPlayer);
        gameManager.setCollisionCallback(this);
        scoreBoardManager = new ScoreBoardManager();
        scoreBoardManager.loadEntries(this);

        lanes = new FrameLayout[]{
                findViewById(R.id.first_Lane),
                findViewById(R.id.second_Lane),
                findViewById(R.id.thired_Lane),
                findViewById(R.id.fourth_Lane),
                findViewById(R.id.fifth_Lane)
        };

        hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };

        totalScoretextView = findViewById(R.id.total_score_text);
        FloatingActionButton leftBtn = findViewById(R.id.left_ARROW_btn);
        FloatingActionButton rightBtn = findViewById(R.id.right_ARROW_btn);

        leftBtn.setOnClickListener(v -> gameManager.moveSki(false));
        rightBtn.setOnClickListener(v -> gameManager.moveSki(true));

        for(FrameLayout lane: lanes){
            lane.removeAllViews();
        }

    }



    private void checkLocationServices() {
        if (!locationHandler.areLocationServicesEnabled(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("Please open your location service to save the results")
                    .setPositiveButton("Location settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel", null)
                    .show();
        }
    }


    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PER_REQ_CODE);
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isPaused) {
            resumeGame();
        }
        if (checkLocationPermission()) {
            locationHandler.startLocationUpdates();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
        locationHandler.stopLocationUpdates();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseGame();
        locationHandler.stopLocationUpdates();
    }

    @Override
    public void onCrash() {
        runOnUiThread(() -> Toast.makeText(this, "Crash! Watch out for trees!", Toast.LENGTH_SHORT).show());
    }
    @Override
    public void onPrize(){
        runOnUiThread(() -> Toast.makeText(this, "Great job! Extra 100 points!", Toast.LENGTH_SHORT).show());
    }

    private void startGame() {
        if (!timerOn) {
            timerOn = true;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> updateGame());
                }
            }, 0, gameDelay);
        }
    }

    private void pauseGame() {
        Log.d(TAG, "stopTimer: Timer Stopped");
        isPaused = true;
        if (timerOn) {
            timerOn = false;
            timer.cancel();
        }
    }

    private void resumeGame() {
        isPaused = false;
        startGame();
    }

    private void resetGame() {
        gameManager = new GameManager(ROWS, COLS, LIVES, soundPlayer);
        updateUserInterface();
        resumeGame();
    }

    private void updateGame() {
        gameManager.updateGame();
        updateUserInterface();
        updateScore();
        updateLives();
        if (gameManager.isGameOver()) {
            showGameOverDialog();
        }
    }
    private void showPauseMenu() {
        pauseGame();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View pauseMenuView = getLayoutInflater().inflate(R.layout.activity_pause_menu, null);
        builder.setView(pauseMenuView);

        pauseMenuView.findViewById(R.id.continue_button).setOnClickListener(v -> {
            resumeGame();
            pauseDialog.dismiss();
        });

        pauseMenuView.findViewById(R.id.start_over_button).setOnClickListener(v -> {
            resetGame();
            pauseDialog.dismiss();
        });

        pauseMenuView.findViewById(R.id.quit_button).setOnClickListener(v -> {
            showQuitConfirmationDialog();
        });

        pauseDialog = builder.create();
        pauseDialog.setCancelable(false);
        pauseDialog.show();
    }

    private void updateScore() {
        totalScoretextView.setText(getString(R.string.totalScoreFmt, gameManager.getTotalScore()));
    }

    private void updateLives() {
        int lives = gameManager.getLives();
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisibility(i < lives ? View.VISIBLE : View.INVISIBLE);
        }
    }


    private void saveScore(String playerName) {
        int combinedScore = gameManager.getTotalScore();
        Date currentTime = new Date();

        if (currentLatitude != 0 && currentLongitude != 0) {
            ScoreboardEntry newEntry = new ScoreboardEntry(playerName, combinedScore, currentLatitude, currentLongitude, currentTime);
            scoreBoardManager.addEntry(newEntry);
            scoreBoardManager.saveEntries(this);
            Log.d(TAG, "New score entry: " + newEntry.toString());
        } else {
            // If we don't have a current location, request a single update
            if (checkLocationPermission()) {
                locationHandler.requestSingleUpdate(new LocationHandler.SingleUpdateCallback() {
                    @Override
                    public void onLocationResult(double latitude, double longitude) {
                        ScoreboardEntry newEntry = new ScoreboardEntry(playerName, combinedScore, latitude, longitude, currentTime);
                        scoreBoardManager.addEntry(newEntry);
                        scoreBoardManager.saveEntries(MainActivity.this); // or TiltcontrolActivity.this
                        Log.d(TAG, "New score entry with updated location: " + newEntry.toString());
                    }
                });
            } else {
                // If we can't get a location, save with default values
                ScoreboardEntry newEntry = new ScoreboardEntry(playerName, combinedScore, 0, 0, currentTime);
                scoreBoardManager.addEntry(newEntry);
                scoreBoardManager.saveEntries(this);
                Log.d(TAG, "New score entry without location: " + newEntry.toString());
            }
        }
    }

    private void showGameOverDialog() {
        pauseGame();
        showSaveScoreDialog("Game Over", gameManager.getTotalScore());
    }

    private void showQuitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Save Result")
                .setMessage("Do you want to save your result before quitting?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    showSaveScoreDialog("Quit Game", gameManager.getTotalScore());
                })
                .setNegativeButton("No", (dialog, which) -> finish())
                .show();
    }

    private void showSaveScoreDialog(String title, int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage("Your score is: " + score + ". Enter your name:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playerName = input.getText().toString();
                if (!playerName.isEmpty()) {
                    saveScore(playerName);
                }
                finish();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }




    private void updateUserInterface() {
        int[][] logicalGrid = gameManager.getBoard();
        for (int i = 0; i < COLS; i++) {
            lanes[i].removeAllViews();
            for (int j = 0; j < ROWS; j++) {
                if (logicalGrid[j][i] != GameManager.RESET) {
                    AppCompatImageView imageView = new AppCompatImageView(this);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    );

                    int objectSize = lanes[i].getWidth() / 2;
                    params.width = objectSize;
                    params.height = objectSize;

                    params.topMargin = j * (lanes[i].getHeight() / ROWS);
                    params.leftMargin = (lanes[i].getWidth() - objectSize) / 2;
                    imageView.setLayoutParams(params);

                    switch (logicalGrid[j][i]) {
                        case GameManager.SKIER:
                            imageView.setImageResource(R.drawable.man_skiing_svgrepo_com);
                            break;
                        case GameManager.TREE:
                            imageView.setImageResource(R.drawable.tree_svgrepo_com);
                            break;
                        case GameManager.CHOCO:
                            imageView.setImageResource(R.drawable.hot_drink_cocoa_svgrepo_com);
                            break;
                    }

                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    lanes[i].addView(imageView);
                }
            }
        }

        totalScoretextView.setText(getString(R.string.totalScoreFmt, gameManager.getTotalScore()));
        updateLives();
    }

    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PER_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationHandler.startLocationUpdates();
            } else {
                Log.d(TAG, "Location permission denied");
                Toast.makeText(this, "Location permission is required for scoreboard features", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationUpdated(double latitude, double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
    }

    @Override
    public void onLocationPermissionRequired() {
        checkLocationPermission();
    }
}