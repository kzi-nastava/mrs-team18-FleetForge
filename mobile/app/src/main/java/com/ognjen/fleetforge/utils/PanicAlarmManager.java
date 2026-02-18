package com.ognjen.fleetforge.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.ognjen.fleetforge.R;

public class PanicAlarmManager {

    private static PanicAlarmManager instance;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private PanicAlarmManager() {}

    public static PanicAlarmManager getInstance() {
        if (instance == null) {
            instance = new PanicAlarmManager();
        }
        return instance;
    }

    public void startAlarm(Context context) {
        if (isPlaying) return;

        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.alarm);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        isPlaying = true;
    }

    public void stopAlarm() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
