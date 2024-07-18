package com.example.skigame.Utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.skigame.Interfaces.MoveCallback;

public class MoveDetector {

    private static final String MD_TAG = "MoveDetector";
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    private int moveAxisX = 0;
    private int moveAxisY = 0;
    private long lastUpdateTime = 0l;
    private MoveCallback moveCallback;

    private long updateInterval = 500;
    private float movementThreshold = 6.0f;


    public MoveDetector (Context context, MoveCallback moveCallback){
        this.moveCallback = moveCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(sensor == null){
            Log.e(MD_TAG, "Accelerometer not available on this device");
        }

        sensorEventListener = initEventListener();
    }

    private SensorEventListener  initEventListener() {

        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = event.values[0];
                    float y = event.values[1];
                    processMovement(x, y);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    private void processMovement(float x, float y) {

        long currentTime = System.currentTimeMillis();
        if(currentTime- lastUpdateTime > updateInterval) {
            lastUpdateTime = currentTime;
            if (Math.abs(x) > movementThreshold) {
                moveAxisX += x > 0? 1 : -1;
                moveCallback.moveX(x>0);
            }
            if (Math.abs(y) > movementThreshold) {
                moveAxisY += y > 0 ?1 : -1;
                moveCallback.moveY(y>0);
            }
        }
    }

    public void start(){

        if(sensor != null) {
            sensorManager.registerListener(
                    sensorEventListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Log.e(MD_TAG, "Failed to start MoveDetector: Accelerometer not available");
        }
    }

    public void stop(){
        sensorManager.unregisterListener(
                sensorEventListener,
                sensor);
    }

    public int getMoveCountX() {
        return moveAxisX;
    }

    public int getMoveCountY() {
        return moveAxisY;
    }

}


