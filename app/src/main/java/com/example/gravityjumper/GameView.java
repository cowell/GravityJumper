// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/GameView.java
package com.example.gravityjumper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private SurfaceHolder holder;
    private volatile boolean playing;
    private Player player;
    private Paint paint;
    private Level currentLevel;
    private GravityDirection currentGravity = GravityDirection.DOWN;
    private boolean isSetup = false;

    // Theme related variable
    private LevelTheme currentTheme;

    // Music manager
    private MusicManager musicManager;

    // Camera/viewport variables
    private float cameraX = 0;
    private float cameraY = 0;
    private int screenWidth;
    private int screenHeight;

    // Score tracking variables
    private int totalScore = 0;
    private int highScore = 0;

    public enum GravityDirection {
        DOWN, UP, LEFT, RIGHT
    }

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        holder = getHolder();
        paint = new Paint();
        paint.setAntiAlias(true);
        musicManager = MusicManager.getInstance(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        Log.d("GameView", "Screen size: " + w + "x" + h);
    }

    private void setupGame() {
        if (!isSetup && screenWidth > 0 && screenHeight > 0) {
            player = new Player(getContext());
            Log.d("GameView", "Player created with size: " + player.getWidth() + "x" + player.getHeight());

            // Load high score
            loadHighScore();

            // Adjust level size to be closer to screen size
            int levelWidth = screenWidth * 2;  // Make level 2x screen width
            int levelHeight = screenHeight * 2; // Make level 2x screen height

            currentLevel = new Level(1, getContext(), levelWidth, levelHeight);

            // Set the initial theme
            currentTheme = LevelTheme.getThemeForLevel(1);

            // Start music for first level
            musicManager.playMusicForTheme(0);

            isSetup = true;

            // Start the player near the center of the level
            player.setX((float) levelWidth / 4);
            player.setY((float) levelHeight / 4);
        }
    }

    private void updateCamera() {
        // Center camera on player
        cameraX = player.getX() + player.getWidth()/2 - (float) screenWidth /2;
        cameraY = player.getY() + player.getHeight()/2 - (float) screenHeight /2;

        // Keep camera within level bounds
        cameraX = Math.max(0, Math.min(cameraX, currentLevel.getLevelWidth() - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, currentLevel.getLevelHeight() - screenHeight));
    }

    @Override
    public void run() {
        while (playing) {
            if (!isSetup && screenWidth > 0 && screenHeight > 0) {
                setupGame();
            }
            if (isSetup) {
                update();
                draw();
            }
            control();
        }
    }

    private void update() {
        player.update(currentGravity);
        currentLevel.checkCollisions(player);
        updateCamera();

        // Check if level is completed
        if (currentLevel.isCompleted()) {
            // Add level score to total score
            totalScore += currentLevel.getScore();

            // Update high score if needed
            if (totalScore > highScore) {
                highScore = totalScore;
                saveHighScore();
            }

            // Create next level with new theme
            int nextLevel = currentLevel.getLevelNumber() + 1;
            currentLevel = new Level(nextLevel, getContext(), currentLevel.getLevelWidth(), currentLevel.getLevelHeight());

            // Update the theme for the new level
            currentTheme = LevelTheme.getThemeForLevel(nextLevel);

            // Start music for new theme
            int themeIndex = (nextLevel - 1) % LevelTheme.getThemes().length;
            musicManager.playMusicForTheme(themeIndex);

            // Reset player position
            player.setX((float) currentLevel.getLevelWidth() / 4);
            player.setY((float) currentLevel.getLevelHeight() / 4);
            player.setVelocityX(0);
            player.setVelocityY(0);
        }
    }

    // Save high score to SharedPreferences
    private void saveHighScore() {
        SharedPreferences prefs = getContext().getSharedPreferences("GravityJumperPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("highScore", highScore);
        editor.apply();
    }

    // Load high score from SharedPreferences
    private void loadHighScore() {
        SharedPreferences prefs = getContext().getSharedPreferences("GravityJumperPrefs", Context.MODE_PRIVATE);
        highScore = prefs.getInt("highScore", 0);
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    // Draw background with theme color
                    canvas.drawColor(currentTheme.backgroundColor);

                    // Save canvas state before translating
                    canvas.save();

                    // Apply camera translation
                    canvas.translate(-cameraX, -cameraY);

                    // Draw level elements with theme colors
                    currentLevel.draw(canvas, paint, currentTheme);

                    // Draw player with theme color (the Player class ignores the color)
                    player.draw(canvas, paint, currentTheme.playerColor);

                    // Debug: Draw a reference point at player position for clarity
                    paint.setColor(Color.GREEN);
                    canvas.drawCircle(player.getX() + player.getWidth()/2,
                            player.getY() + player.getHeight()/2,
                            5, paint);

                    // Restore canvas to original state
                    canvas.restore();

                    // Draw HUD elements with theme text color
                    paint.setColor(currentTheme.textColor);
                    paint.setTextSize(40);

                    // Draw theme name
                    canvas.drawText("Theme: " + currentTheme.themeName, 20, 60, paint);

                    // Draw current gravity direction
                    canvas.drawText("Gravity: " + currentGravity.toString(), 20, 110, paint);

                    // Draw level number
                    canvas.drawText("Level: " + currentLevel.getLevelNumber(), 20, 160, paint);

                    // Draw current level score
                    canvas.drawText("Level Score: " + currentLevel.getScore(), 20, 210, paint);

                    // Draw total score
                    canvas.drawText("Total Score: " + totalScore, 20, 260, paint);

                    // Draw high score
                    canvas.drawText("High Score: " + highScore, 20, 310, paint);

                    // Draw direction indicator arrow with theme color
                    paint.setColor(currentTheme.textColor);
                    paint.setStrokeWidth(5);
                    float arrowSize = 60;
                    float centerX = (float) screenWidth / 2;
                    float centerY = screenHeight - 100;

                    // Draw arrow pointing in current gravity direction
                    switch (currentGravity) {
                        case UP:
                            canvas.drawLine(centerX, centerY, centerX, centerY - arrowSize, paint);
                            canvas.drawLine(centerX, centerY - arrowSize, centerX - arrowSize/2, centerY - arrowSize/2, paint);
                            canvas.drawLine(centerX, centerY - arrowSize, centerX + arrowSize/2, centerY - arrowSize/2, paint);
                            break;
                        case DOWN:
                            canvas.drawLine(centerX, centerY, centerX, centerY + arrowSize, paint);
                            canvas.drawLine(centerX, centerY + arrowSize, centerX - arrowSize/2, centerY + arrowSize/2, paint);
                            canvas.drawLine(centerX, centerY + arrowSize, centerX + arrowSize/2, centerY + arrowSize/2, paint);
                            break;
                        case LEFT:
                            canvas.drawLine(centerX, centerY, centerX - arrowSize, centerY, paint);
                            canvas.drawLine(centerX - arrowSize, centerY, centerX - arrowSize/2, centerY - arrowSize/2, paint);
                            canvas.drawLine(centerX - arrowSize, centerY, centerX - arrowSize/2, centerY + arrowSize/2, paint);
                            break;
                        case RIGHT:
                            canvas.drawLine(centerX, centerY, centerX + arrowSize, centerY, paint);
                            canvas.drawLine(centerX + arrowSize, centerY, centerX + arrowSize/2, centerY - arrowSize/2, paint);
                            canvas.drawLine(centerX + arrowSize, centerY, centerX + arrowSize/2, centerY + arrowSize/2, paint);
                            break;
                    }
                    paint.setStrokeWidth(1);

                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void control() {
        try {
            Thread.sleep(17); // ~60fps
        } catch (InterruptedException e) {
            Log.e("GameView", "Thread interrupted during game loop", e);
            // Optionally restore the interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public void pause() {
        playing = false;
        musicManager.pauseMusic();
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            Log.e("GameView", "Thread interrupted during game loop", e);
        }
    }

    public void resume() {
        playing = true;
        musicManager.resumeMusic();
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Simplified direct gravity control
    public void flipGravity() {
        // Cycle through all four directions
        switch (currentGravity) {
            case DOWN:
                setGravityDirection(GravityDirection.UP);
                break;
            case UP:
                setGravityDirection(GravityDirection.RIGHT);
                break;
            case RIGHT:
                setGravityDirection(GravityDirection.LEFT);
                break;
            case LEFT:
                setGravityDirection(GravityDirection.DOWN);
                break;
        }
    }

    private void setGravityDirection(GravityDirection newDirection) {
        if (currentGravity != newDirection) {
            currentGravity = newDirection;
            // Play flip sound
            SoundManager.getInstance(getContext()).playFlipSound();
        }
    }

    // Split the screen into quadrants for directional control
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Determine which quadrant of the screen was touched
            float x = event.getX();
            float y = event.getY();

            // Left side = LEFT gravity
            if (x < (float) screenWidth / 2 && y > (float) screenHeight / 4 && y < (float) (screenHeight * 3) /4) {
                setGravityDirection(GravityDirection.LEFT);
            }
            // Right side = RIGHT gravity
            else if (x > (float) screenWidth / 2 && y > (float) screenHeight / 4 && y < (float) (screenHeight * 3) /4) {
                setGravityDirection(GravityDirection.RIGHT);
            }
            // Top area = UP gravity
            else if (y < (float) screenHeight / 2) {
                setGravityDirection(GravityDirection.UP);
            }
            // Bottom area = DOWN gravity
            else {
                setGravityDirection(GravityDirection.DOWN);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    // Method to get current score for MainActivity
    public int getTotalScore() {
        return totalScore;
    }

    // Method to get high score for MainActivity
    public int getHighScore() {
        return highScore;
    }
}