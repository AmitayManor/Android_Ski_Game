package com.example.skigame;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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

import com.example.skigame.Logic.GameManager;
import com.example.skigame.Managers.ScoreBoardManager;
import com.example.skigame.Models.ScoreboardEntry;
import com.example.skigame.Utils.LocationHandler;
import com.example.skigame.Utils.SoundPlayer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;


public class TiltcontrolActivity extends AppCompatActivity implements SensorEventListener, LocationHandler.LocationUpdateListener{

    private static final String TAG = "TiltControlActivity";
    private static final int ROWS = 8;
    private static final int COLS = 5;
    private static final int LIVES = 3;
    private static final long DELAY = 500;
    private static final float TILT_THRESHOLD = 1.0f;
    private static final float MAX_TILT = 10.0f;
    private static final long MOVE_DELAY = 100;

    private static final int LOCATION_PER_REQ_CODE = 1000;
    protected LocationHandler locationHandler;
    protected double currentLongitude;
    protected double currentLatitude;

    private GameManager gameManager;
    private FrameLayout[] lanes;
    private AppCompatImageView[] hearts;
    private TextView totalScoretextView;
    private FloatingActionButton pauseButton;
    private AlertDialog pauseDialog;
    private boolean isPaused = false;

    private boolean timerOn = false;
    private Timer timer;
    private SoundPlayer soundPlayer;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastMoveTime = 0;

    private ScoreBoardManager scoreBoardManager;
    private FusedLocationProviderClient fusedLocationClient;


   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundPlayer = new SoundPlayer(this);
        gameManager = new GameManager(ROWS, COLS, LIVES, soundPlayer);
        scoreBoardManager = new ScoreBoardManager();
        scoreBoardManager.loadEntries(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationHandler = new LocationHandler(this);
        locationHandler.setLocationUpdateListener((LocationHandler.LocationUpdateListener) this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(v -> showPauseMenu());

        checkLocationServices();

        initializeViews();
    }


  
    private void initializeViews() {

        soundPlayer = new SoundPlayer(this);
        gameManager = new GameManager(ROWS,COLS, LIVES, soundPlayer);
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
        findViewById(R.id.left_ARROW_btn).setVisibility(View.GONE);
        findViewById(R.id.right_ARROW_btn).setVisibility(View.GONE);

        for (FrameLayout lane : lanes) {
            lane.removeAllViews();
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
    
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        if (checkLocationPermission()) {
            locationHandler.startLocationUpdates();
        }
        if (!isPaused) {
            resumeGame();
        }
    }


    
    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
        sensorManager.unregisterListener(this);
        locationHandler.stopLocationUpdates();
    }

 
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseGame();
        locationHandler.stopLocationUpdates();
    }

    
    private void startGame() {
        Log.d(TAG, "startTimer: Timer Started");
        if (!timerOn) {
            timerOn = true;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> updateGame());
                }
            }, 0, DELAY);
        }
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

    private void updateScore() {
        totalScoretextView.setText(getString(R.string.totalScoreFmt, gameManager.getTotalScore()));
    }
   
    private void pauseGame() {
        isPaused = true;
        Log.d(TAG, "stopTimer: Timer Stopped");
        if (timerOn) {
            timerOn = false;
            timer.cancel();
        }
        sensorManager.unregisterListener(this);
    }
    private void resumeGame() {
        isPaused = false;
        startGame();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    private void resetGame() {
        gameManager = new GameManager(ROWS, COLS, LIVES, soundPlayer);
        updateUserInterface();
        resumeGame();
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

    private void updateLives() {
        int lives = gameManager.getLives();
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisibility(i < lives ? View.VISIBLE : View.INVISIBLE);
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

    private void showGameOverDialog() {
        pauseGame();
        showSaveScoreDialog("Game Over", gameManager.getTotalScore());
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

    private void saveScore(String playerName) {
        int combinedScore = gameManager.getTotalScore();
        Date currentTime = new Date();
        ScoreboardEntry newEntry = new ScoreboardEntry(playerName, combinedScore, currentLatitude, currentLongitude, currentTime);
        scoreBoardManager.addEntry(newEntry);
        scoreBoardManager.saveEntries(this);
        Log.d(TAG, "New score entry: " + newEntry.toString());
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float tiltX = event.values[0];
            long currentTime = System.currentTimeMillis();

            tiltX = Math.max(-MAX_TILT, Math.min(tiltX, MAX_TILT));

            float moveProbability = Math.abs(tiltX) / MAX_TILT;

            if (currentTime - lastMoveTime > MOVE_DELAY) {
                if (Math.random() < moveProbability) {
                    if (tiltX > TILT_THRESHOLD) {
                        gameManager.moveSki(true);
                        lastMoveTime = currentTime;
                    } else if (tiltX < -TILT_THRESHOLD) {
                        gameManager.moveSki(false);
                        lastMoveTime = currentTime;
                    }
                }
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
