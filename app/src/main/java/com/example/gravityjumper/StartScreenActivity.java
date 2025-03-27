// StartScreenActivity.java
package com.example.gravityjumper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class StartScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        // Hide system UI for immersive experience
        hideSystemUI();

        // Get the default theme (first theme)
        LevelTheme defaultTheme = LevelTheme.getThemeForLevel(1);

        // Set the player image based on the theme
        ImageView logoImage = findViewById(R.id.logoImage);

        // Set the appropriate player image based on theme
        int playerResourceId = getPlayerResourceForTheme(defaultTheme.themeName);
        logoImage.setImageResource(playerResourceId);

        // Set background color based on theme
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(defaultTheme.backgroundColor);

        // Set text color for title and version based on theme
        TextView titleText = findViewById(R.id.titleText);
        TextView versionText = findViewById(R.id.versionText);
        titleText.setTextColor(defaultTheme.textColor);
        versionText.setTextColor(defaultTheme.textColor);

        // Setup start button
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Setup settings button
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch settings activity when implemented
                // Intent intent = new Intent(StartScreenActivity.this, SettingsActivity.class);
                // startActivity(intent);
            }
        });
    }

    // Helper method to get the correct player resource based on theme name
    private int getPlayerResourceForTheme(String themeName) {
        switch(themeName) {
            case "Classic":
                return R.drawable.player_classic;
            case "Space":
                return R.drawable.player_space;
            case "Underwater":
                return R.drawable.player_underwater;
            case "Lava":
                return R.drawable.player_lava;
            case "Forest":
                return R.drawable.player_forest;
            default:
                // Fallback to app icon if theme doesn't match
                return R.mipmap.ic_launcher;
        }
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
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }
}