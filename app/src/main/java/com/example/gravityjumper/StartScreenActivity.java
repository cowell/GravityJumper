// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/StartScreenActivity.java

package com.example.gravityjumper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        setContentView(R.layout.activity_start_screen);

        // Hide the action bar for fullscreen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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
}
