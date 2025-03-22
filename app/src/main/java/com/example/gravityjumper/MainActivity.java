// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/MainActivity.java

package com.example.gravityjumper;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
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

        // Set immersive fullscreen mode (hides navigation bar)
        hideSystemUI();

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

    private void hideSystemUI() {
        // Modern API for Android 11+
        getWindow().setDecorFitsSystemWindows(false);
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

    // Add method to show score dialog
    private void showScoreDialog(int score, int highScore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Progress");
        builder.setMessage("Your Score: " + score + "\nHigh Score: " + highScore);
        builder.setPositiveButton("Continue", null);
        builder.show();
    }
}