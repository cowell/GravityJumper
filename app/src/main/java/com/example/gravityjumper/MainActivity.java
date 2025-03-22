// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/MainActivity.java

package com.example.gravityjumper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private Button flipGravityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the action bar for fullscreen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        flipGravityButton = findViewById(R.id.flipGravityButton);

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
    }

    // Add method to show score dialog
    private void showScoreDialog(int score, int highScore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Progress");
        builder.setMessage("Your Score: " + score + "\nHigh Score: " + highScore);
        builder.setPositiveButton("Continue", null);
        builder.show();
    }
}