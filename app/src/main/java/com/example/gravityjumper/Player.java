// Player.java
package com.example.gravityjumper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class Player {
    private Bitmap bitmap;
    private Bitmap originalBitmap; // Store the original bitmap for transformations
    private float x, y;
    private float velocityX, velocityY;
    private int width = 100;  // Default size if bitmap fails to load
    private int height = 100;
    private final RectF boundingBox;

    // Squash and stretch variables
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float targetScaleX = 1.0f;
    private float targetScaleY = 1.0f;
    private float jiggleTimer = 0;
    private boolean isJiggling = false;
    private float jiggleIntensity = 0;
    private float rotation = 0;

    // Animation speeds
    private final float SQUASH_RECOVERY_SPEED = 0.1f;
    private final float JIGGLE_DECAY = 0.9f;

    public Player(Context context) {
        try {
            // Load the default player image (classic theme)
            originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_classic);

            if (originalBitmap != null) {
                // Scale the bitmap to be larger
                originalBitmap = Bitmap.createScaledBitmap(originalBitmap, 150, 150, true);
                bitmap = originalBitmap;

                width = bitmap.getWidth();
                height = bitmap.getHeight();
                Log.d("Player", "Bitmap loaded successfully: " + width + "x" + height);
            } else {
                Log.e("Player", "Failed to load bitmap - returned null");
            }
        } catch (Exception e) {
            Log.e("Player", "Failed to load player bitmap: " + e.getMessage());
            originalBitmap = null;
            bitmap = null;
        }

        // Set default size if bitmap fails to load
        if (bitmap == null) {
            width = 150;
            height = 150;
        }

        x = 100;
        y = 100;
        velocityX = 0;
        velocityY = 0;
        boundingBox = new RectF(x, y, x + width, y + height);
    }

    // Add method to update player bitmap based on theme
    public void updatePlayerBitmap(Context context, String themeName) {
        int resourceId;

        // Select the appropriate player image based on theme
        switch(themeName) {
            case "Classic":
                resourceId = R.drawable.player_classic;
                break;
            case "Space":
                resourceId = R.drawable.player_space;
                break;
            case "Underwater":
                resourceId = R.drawable.player_underwater;
                break;
            case "Lava":
                resourceId = R.drawable.player_lava;
                break;
            case "Forest":
                resourceId = R.drawable.player_forest;
                break;
            default:
                resourceId = R.drawable.player_classic;
                break;
        }

        try {
            // Load the themed player image
            originalBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);

            if (originalBitmap != null) {
                // Scale the bitmap to be larger - increase these values
                originalBitmap = Bitmap.createScaledBitmap(originalBitmap, 150, 150, true);
                bitmap = originalBitmap;

                width = bitmap.getWidth();
                height = bitmap.getHeight();
                Log.d("Player", "Themed bitmap loaded successfully: " + width + "x" + height);
            }
        } catch (Exception e) {
            Log.e("Player", "Failed to load themed player bitmap: " + e.getMessage());
        }
    }



    // Player.java - update the update method
    public void update(GameView.GravityDirection gravity) {
        // Apply gravity based on current direction
        // Adjust as needed
        float GRAVITY_FORCE = 0.5f;
        switch (gravity) {
            case DOWN:
                velocityY += GRAVITY_FORCE;
                break;
            case UP:
                velocityY -= GRAVITY_FORCE;
                break;
            case LEFT:
                velocityX -= GRAVITY_FORCE;
                break;
            case RIGHT:
                velocityX += GRAVITY_FORCE;
                break;
        }

        // Apply velocity
        x += velocityX;
        y += velocityY;

        // Apply more drag for better control
        velocityX *= 0.95f; // More drag (was 0.98f)
        velocityY *= 0.95f; // More drag (was 0.98f)

        // Limit maximum velocity to prevent tunneling through obstacles
        float MAX_VELOCITY = 15.0f;
        velocityX = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, velocityX));
        velocityY = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, velocityY));

        // Update bounding box for collision detection
        boundingBox.set(x, y, x + width, y + height);

        // Update blob animation effects
        updateBlobAnimation(gravity);
    }



    private void updateBlobAnimation(GameView.GravityDirection gravity) {
        // Calculate movement speed for stretch effect
        float speed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);

        // Calculate stretch factor based on movement
        if (!isJiggling) {
            switch (gravity) {
                case DOWN:
                case UP:
                    // Vertical movement - stretch vertically
                    targetScaleX = 1.0f - Math.min(0.2f, Math.abs(velocityY) * 0.02f);
                    targetScaleY = 1.0f + Math.min(0.3f, Math.abs(velocityY) * 0.03f);
                    break;
                case LEFT:
                case RIGHT:
                    // Horizontal movement - stretch horizontally
                    targetScaleX = 1.0f + Math.min(0.3f, Math.abs(velocityX) * 0.03f);
                    targetScaleY = 1.0f - Math.min(0.2f, Math.abs(velocityX) * 0.02f);
                    break;
            }
        }

        // Smoothly animate toward target scale
        scaleX += (targetScaleX - scaleX) * SQUASH_RECOVERY_SPEED;
        scaleY += (targetScaleY - scaleY) * SQUASH_RECOVERY_SPEED;

        // Handle jiggle animation
        if (isJiggling) {
            jiggleTimer += 0.2f;

            // Apply sine-wave based rotation for jiggle effect
            rotation = (float) Math.sin(jiggleTimer) * jiggleIntensity;

            // Decay jiggle intensity
            jiggleIntensity *= JIGGLE_DECAY;

            // Stop jiggling when intensity is very low
            if (jiggleIntensity < 0.1f) {
                isJiggling = false;
                rotation = 0;
            }
        }

        // Create transformed bitmap for animation effects
        updateTransformedBitmap();
    }

    private void updateTransformedBitmap() {
        if (originalBitmap == null) return;

        Matrix matrix = new Matrix();

        // Apply scaling around center point
        matrix.postScale(scaleX, scaleY, originalBitmap.getWidth() / 2f, originalBitmap.getHeight() / 2f);

        // Apply rotation for jiggle effect
        if (isJiggling) {
            matrix.postRotate(rotation, originalBitmap.getWidth() / 2f, originalBitmap.getHeight() / 2f);
        }

        // Create transformed bitmap
        bitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
    }



    public void draw(Canvas canvas, Paint paint, int playerColor) {
        // Ignore the playerColor parameter and draw with original appearance
        if (bitmap != null) {
            // Draw the transformed bitmap with original colors
            canvas.drawBitmap(bitmap, x, y, paint);
        } else {
            // Fallback to a highly visible player rectangle if bitmap fails
            paint.setColor(Color.RED);
            canvas.drawRect(boundingBox, paint);

            // Draw an X to make it distinct
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(3);
            canvas.drawLine(x, y, x + width, y + height, paint);
            canvas.drawLine(x + width, y, x, y + height, paint);
            paint.setStrokeWidth(1);
        }
    }



    // Collision response methods with jiggle effect - less bouncy
    public void bounceX() {
        velocityX = -velocityX * 0.5f; // Less bouncy (was 0.6f)
        startJiggle();

        // Squash horizontally on impact
        targetScaleX = 0.7f;
        targetScaleY = 1.3f;
    }

    public void bounceY() {
        velocityY = -velocityY * 0.3f; // Even less bouncy (was 0.6f)
        startJiggle();

        // Squash vertically on impact
        targetScaleX = 1.3f;
        targetScaleY = 0.7f;
    }

    private void startJiggle() {
        isJiggling = true;
        jiggleTimer = 0;
        jiggleIntensity = 15.0f; // Starting rotation amount in degrees
    }



    // Getters and setters for position
    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) {
        this.x = x;
        boundingBox.set(x, y, x + width, y + height);
    }
    public void setY(float y) {
        this.y = y;
        boundingBox.set(x, y, x + width, y + height);
    }

    // Getters and setters for velocity
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public void setVelocityX(float velocityX) { this.velocityX = velocityX; }
    public void setVelocityY(float velocityY) { this.velocityY = velocityY; }

    // Getters for dimensions
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public RectF getBoundingBox() { return boundingBox; }

    public void resolveCollision(float newX, float newY) {
        // Set new position
        this.x = newX;
        this.y = newY;
    }
}
