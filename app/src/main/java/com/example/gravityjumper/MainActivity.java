// MainActivity.java
package com.example.gravityjumper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.WindowCompat;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String CURRENT_THEME_KEY = "CurrentTheme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the action bar for fullscreen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set immersive fullscreen mode (hides navigation bar)
        hideSystemUI();

        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);

        // Load saved theme before game starts
        LevelTheme savedTheme = loadSavedTheme();
        // Pass the saved theme to GameView
        gameView.setInitialTheme(savedTheme);

        Button flipGravityButton = findViewById(R.id.flipGravityButton);

        flipGravityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.flipGravity();
            }
        });

        // Register callback for handling back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Show score before exiting
                showScoreDialog(gameView.getTotalScore(), gameView.getHighScore());
                // Call this to continue with the back action
                this.setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Setup music toggle button
        setupMusicToggle();
    }

    // Method to load saved theme
    private LevelTheme loadSavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedThemeName = prefs.getString(CURRENT_THEME_KEY, null);

        if (savedThemeName != null) {
            // Find the theme by name
            for (LevelTheme theme : LevelTheme.THEMES) {
                if (theme.themeName.equals(savedThemeName)) {
                    return theme;
                }
            }
        }

        // Default to first theme if saved theme not found
        return LevelTheme.getThemeForLevel(1);
    }

    private void hideSystemUI() {
        // Use WindowCompat instead of the deprecated setDecorFitsSystemWindows
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
        // Re-hide system UI when resuming
        hideSystemUI();
    }

    private void setupMusicToggle() {
        Button musicToggleButton = findViewById(R.id.music_toggle_button);
        updateMusicButtonText(musicToggleButton);

        musicToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicManager.getInstance(MainActivity.this).toggleMusic();
                updateMusicButtonText(musicToggleButton);
            }
        });
    }

    private void updateMusicButtonText(Button button) {
        boolean musicEnabled = MusicManager.getInstance(this).isMusicEnabled();
        button.setText(musicEnabled ? "Music: ON" : "Music: OFF");
    }

    public void onThemeButtonClick(View view) {
        Intent intent = new Intent(this, ThemeSelectionActivity.class);
        startActivity(intent);
    }

    // Method to show score dialog
    private void showScoreDialog(int score, int highScore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Progress");
        builder.setMessage("Your Score: " + score + "\nHigh Score: " + highScore);
        builder.setPositiveButton("Continue", null);
        builder.show();
    }
}