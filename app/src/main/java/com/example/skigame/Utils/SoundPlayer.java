package com.example.skigame.Utils;

import android.content.Context;
import android.media.MediaPlayer;


public class SoundPlayer {

    private final Context context;
    private MediaPlayer sfx;

    public SoundPlayer(Context context) {this.context = context;}


    public void playSound (int resID){

        stopSound();
        sfx = MediaPlayer.create(context, resID);
        if(sfx != null) {
            sfx.setOnCompletionListener(mp -> {
                mp.release();
                sfx = null;
            });
            sfx.start();
        }
    }

    private void stopSound() {
        if(sfx != null){
            sfx.stop();
            sfx.release();
            sfx = null;
        }

    }

    public void release() {stopSound();}
}
