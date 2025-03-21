// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/GameView.java

package com.example.gravityjumper;

import android.content.Context;
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

    // Camera/viewport variables
    private float cameraX = 0;
    private float cameraY = 0;
    private int screenWidth;
    private int screenHeight;

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

            // Adjust level size to be closer to screen size
            int levelWidth = screenWidth * 2;  // Make level 2x screen width
            int levelHeight = screenHeight * 2; // Make level 2x screen height

            currentLevel = new Level(1, getContext(), levelWidth, levelHeight);
            isSetup = true;

            // Start the player near the center of the level
            player.setX(levelWidth / 4);
            player.setY(levelHeight / 4);
        }
    }

    private void updateCamera() {
        // Center camera on player
        cameraX = player.getX() + player.getWidth()/2 - screenWidth/2;
        cameraY = player.getY() + player.getHeight()/2 - screenHeight/2;

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
            // For now, just restart the level
            int nextLevel = currentLevel.getLevelNumber() + 1;
            currentLevel = new Level(nextLevel, getContext(), currentLevel.getLevelWidth(), currentLevel.getLevelHeight());

            // Reset player position
            player.setX(currentLevel.getLevelWidth() / 4);
            player.setY(currentLevel.getLevelHeight() / 4);
            player.setVelocityX(0);
            player.setVelocityY(0);
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    // Draw background
                    canvas.drawColor(Color.BLACK);

                    // Save canvas state before translating
                    canvas.save();

                    // Apply camera translation
                    canvas.translate(-cameraX, -cameraY);

                    // Draw level elements
                    currentLevel.draw(canvas, paint);

                    // Draw player
                    player.draw(canvas, paint);

                    // Debug: Draw a reference point at player position for clarity
                    paint.setColor(Color.GREEN);
                    canvas.drawCircle(player.getX() + player.getWidth()/2,
                            player.getY() + player.getHeight()/2,
                            5, paint);

                    // Restore canvas to original state
                    canvas.restore();

                    // Draw HUD elements if needed
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(30);
                    canvas.drawText("Gravity: " + currentGravity.toString(), 20, 50, paint);

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
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void flipGravity() {
        // Cycle through gravity directions
        switch (currentGravity) {
            case DOWN:
                currentGravity = GravityDirection.UP;
                break;
            case UP:
                currentGravity = GravityDirection.LEFT;
                break;
            case LEFT:
                currentGravity = GravityDirection.RIGHT;
                break;
            case RIGHT:
                currentGravity = GravityDirection.DOWN;
                break;
        }

        // Play flip sound
        SoundManager.getInstance(getContext()).playFlipSound();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Allow tapping the screen to flip gravity too
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            flipGravity();
            return true;
        }
        return super.onTouchEvent(event);
    }
}