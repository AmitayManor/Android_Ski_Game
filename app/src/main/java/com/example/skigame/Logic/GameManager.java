package com.example.skigame.Logic;


import com.example.skigame.R;
import com.example.skigame.Utils.SoundPlayer;
import java.util.Random;
import com.example.skigame.Interfaces.CollisionCallback;

public class GameManager {

    public static final int RESET = 0;
    public static final int SKIER = 1;
    public static final int TREE = 2;
    public static final int CHOCO = 3;
    private static final int DISTANCE_WEIGHT = 1;
    private static final int SCORE_WEIGHT = 10;
    private static final int CHOCO_PROBABILITY = 45;
    private static final int TREE_PROBABILITY = 75;
    private static final double DEFAULT_SPEED = 1.0;
    private int[][] board;
    private int rows;
    private int cols;
    private int score;
    private int lives;
    private int distance;
    private int skiLane;
    private Random random;
    private SoundPlayer soundPlayer;
    private int totalScore;
    private double speedMultiplier;
    private CollisionCallback collisionCallback;

    public GameManager(int rows, int cols, int lives, SoundPlayer soundPlayer) {
        this.rows = rows;
        this.cols = cols;
        this.lives = lives;
        this.random = new Random();
        this.soundPlayer = soundPlayer;
        this.board = new int[rows][cols];
        this.speedMultiplier = DEFAULT_SPEED;
        resetGame();
    }

    public GameManager(int rows, int cols, int lives, SoundPlayer soundPlayer, double speedMultiplier) {
        this.rows = rows;
        this.cols = cols;
        this.lives = lives;
        this.random = new Random();
        this.soundPlayer = soundPlayer;
        this.board = new int[rows][cols];
        this.speedMultiplier = speedMultiplier;
        resetGame();
    }

    private void resetGame() {

        for (int i=0; i<rows; i++){
            for (int j=0; j<cols; j++){
                board[i][j] = RESET;
            }
        }

        skiLane = cols/2;
        board[rows-1][skiLane] = SKIER;
        score =0 ;
        lives = 3;
        distance=0;
    }

    public void updateGame(){
        moveDownObjects();
        respawnObject();
        checkCollisions();
        updateDistance();
        updateTotalScore();

    }
    private void updateDistance(){
        distance += speedMultiplier;;
    }

    private void updateTotalScore() {
        totalScore = (distance*DISTANCE_WEIGHT)+(score*SCORE_WEIGHT);
    }

    private void checkCollisions() {
        if (board[rows-1][skiLane] == TREE) {
            lives--;
            soundPlayer.playSound(R.raw.crash);
            if (collisionCallback != null) {
                collisionCallback.onCrash();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (board[rows-1][skiLane] == CHOCO) {
            soundPlayer.playSound(R.raw.win);
            if (collisionCallback != null) {
                collisionCallback.onPrize();
            }
            score += 10;
        }
        board[rows-1][skiLane] = SKIER;
    }


    private void respawnObject() {

        if(random.nextInt(100)<CHOCO_PROBABILITY){
            int randCol = random.nextInt(cols);
        board[RESET][randCol] = random.nextInt(100) < TREE_PROBABILITY ? TREE : CHOCO;
        }

    }

    private void moveDownObjects() {

        for(int i=rows-1; i>0; i--){
            System.arraycopy(board[i-1], RESET, board[i], RESET, cols);
        }
        for(int i= 0; i<cols;i++){
            board[RESET][i] = RESET;
        }

    }

    public void moveSki (boolean moveRight) {

        if(skiLane <0 || skiLane >=cols){
            throw new IllegalStateException("Skier is out of bounds!");
        }

        board[rows-1][skiLane] = RESET;
        if(moveRight && skiLane < cols -1){
            skiLane++;
        }else if(!moveRight && skiLane >0 ){
            skiLane--;
        }
        board[rows-1][skiLane] = SKIER;
    }

    public int[][] getBoard (){ return board;}
    public int getLives(){return lives;}
    public int getScore(){return score;}
    public int getDistance(){return distance;}
    public boolean isGameOver(){return lives<=0;}
    public int getTotalScore(){return totalScore;}
    public void setCollisionCallback(CollisionCallback callback) {
        this.collisionCallback = callback;
    }
}
