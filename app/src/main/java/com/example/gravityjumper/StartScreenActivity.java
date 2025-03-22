// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/StartScreenActivity.java

package com.example.gravityjumper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the action bar for fullscreen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set immersive fullscreen mode
        hideSystemUI();

        setContentView(R.layout.activity_start_screen);

        // Set up button listeners
        Button playButton = findViewById(R.id.playButton);
        Button exitButton = findViewById(R.id.exitButton);
        TextView titleText = findViewById(R.id.titleText);
        ImageView logoImage = findViewById(R.id.logoImage);

        // Create animations
        Animation pulse = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        pulse.setDuration(2000);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(Animation.INFINITE);

        // Apply animation to logo
        logoImage.startAnimation(pulse);

        // Play button starts the game
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Exit button closes the app
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // For older Android versions
            View decorView = getWindow().getDecorView();
            int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(flags);
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
    protected void onResume() {
        super.onResume();
        // Re-hide system UI when resuming
        hideSystemUI();
    }
}