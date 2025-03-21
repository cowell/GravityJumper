// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/SoundManager.java

package com.example.gravityjumper;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

public class SoundManager {
    private static SoundManager instance;
    private SoundPool soundPool;
    private int flipSoundId = -1;
    private int collectSoundId = -1;
    private int levelCompleteSoundId = -1;
    private boolean soundsLoaded = false;

    private SoundManager(Context context) {
        if (context != null) {
            try {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

                soundPool = new SoundPool.Builder()
                        .setMaxStreams(5)
                        .setAudioAttributes(audioAttributes)
                        .build();

                soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
                    soundsLoaded = true;
                });

                // Load the sound files that you've already added
                flipSoundId = soundPool.load(context, R.raw.flip_sound, 1);
                collectSoundId = soundPool.load(context, R.raw.collect_sound, 1);
                levelCompleteSoundId = soundPool.load(context, R.raw.level_complete, 1);

                Log.d("SoundManager", "Sound system initialized with sound files");
            } catch (Exception e) {
                Log.e("SoundManager", "Error initializing sound manager: " + e.getMessage());
            }
        }
    }

    // Rest of the SoundManager class remains the same
    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void playFlipSound() {
        playSound(flipSoundId);
    }

    public void playCollectSound() {
        playSound(collectSoundId);
    }

    public void playLevelCompleteSound() {
        playSound(levelCompleteSoundId);
    }

    private void playSound(int soundId) {
        if (soundsLoaded && soundId > 0 && soundPool != null) {
            try {
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                Log.e("SoundManager", "Error playing sound: " + e.getMessage());
            }
        }
    }

    public void release() {
        if (soundPool != null) {
            try {
                soundPool.release();
            } catch (Exception e) {
                Log.e("SoundManager", "Error releasing sound pool: " + e.getMessage());
            }
            soundPool = null;
        }
        instance = null;
    }
}