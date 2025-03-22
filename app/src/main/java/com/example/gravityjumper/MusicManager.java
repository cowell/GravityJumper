// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/MusicManager.java
package com.example.gravityjumper;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private Context context;
    private boolean isMusicEnabled = true;
    private int currentMusicRes = -1;

    // Music tracks for different themes
    private static final int[] THEME_MUSIC = {
            R.raw.classic_theme,    // Classic theme music
            R.raw.desert_theme,     // Desert theme music
            R.raw.ice_theme,        // Ice theme music
            R.raw.forest_theme,     // Forest theme music
            R.raw.space_theme       // Space theme music
    };

    private MusicManager(Context context) {
        this.context = context.getApplicationContext();
        // Load music preference
        SharedPreferences prefs = context.getSharedPreferences("GravityJumperPrefs", Context.MODE_PRIVATE);
        isMusicEnabled = prefs.getBoolean("musicEnabled", true);
    }

    public static synchronized MusicManager getInstance(Context context) {
        if (instance == null) {
            instance = new MusicManager(context);
        }
        return instance;
    }

    public void playMusicForTheme(int themeIndex) {
        if (!isMusicEnabled) return;

        // Get music resource based on theme
        int musicRes = THEME_MUSIC[themeIndex % THEME_MUSIC.length];

        // If same music is already playing, do nothing
        if (mediaPlayer != null && currentMusicRes == musicRes && mediaPlayer.isPlaying()) {
            return;
        }

        // Stop current music if playing
        stopMusic();

        try {
            mediaPlayer = MediaPlayer.create(context, musicRes);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.start();
                currentMusicRes = musicRes;
            }
        } catch (Exception e) {
            Log.e("MusicManager", "Error playing music: " + e.getMessage());
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (isMusicEnabled && mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            currentMusicRes = -1;
        }
    }

    public void toggleMusic() {
        isMusicEnabled = !isMusicEnabled;

        // Save preference
        SharedPreferences prefs = context.getSharedPreferences("GravityJumperPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("musicEnabled", isMusicEnabled);
        editor.apply();

        if (isMusicEnabled) {
            resumeMusic();
        } else {
            pauseMusic();
        }
    }

    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }
}
