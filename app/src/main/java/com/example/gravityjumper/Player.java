// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/Player.java

package com.example.gravityjumper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class Player {
    private Bitmap bitmap;
    private float x, y;
    private float velocityX, velocityY;
    private final float GRAVITY_FORCE = 0.5f;
    private int width = 60;  // Larger default size for better visibility
    private int height = 60;
    private RectF boundingBox;

    public Player(Context context) {
        try {
            // Load the player image from resources
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);

            if (bitmap != null) {
                // If bitmap is too large or too small, resize it to something reasonable
                if (bitmap.getWidth() > 200 || bitmap.getHeight() > 200) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
                } else if (bitmap.getWidth() < 30 || bitmap.getHeight() < 30) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
                }

                width = bitmap.getWidth();
                height = bitmap.getHeight();
                Log.d("Player", "Bitmap loaded successfully: " + width + "x" + height);
            } else {
                Log.e("Player", "Failed to load bitmap - returned null");
            }
        } catch (Exception e) {
            Log.e("Player", "Failed to load player bitmap: " + e.getMessage());
            bitmap = null; // Ensure bitmap is null so fallback is used
        }

        x = 100;
        y = 100;
        velocityX = 0;
        velocityY = 0;
        boundingBox = new RectF(x, y, x + width, y + height);
    }

    public void update(GameView.GravityDirection gravity) {
        // Apply gravity based on current direction
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

        // Apply simple drag
        velocityX *= 0.98f;
        velocityY *= 0.98f;

        // Update bounding box for collision detection
        boundingBox.set(x, y, x + width, y + height);
    }

    public void draw(Canvas canvas, Paint paint) {
        if (bitmap != null) {
            // Draw the actual bitmap
            canvas.drawBitmap(bitmap, x, y, paint);

            // Optional: Draw a frame around the player for clarity
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            canvas.drawRect(boundingBox, paint);
            paint.setStyle(Paint.Style.FILL);
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
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public RectF getBoundingBox() { return boundingBox; }

    // Collision response methods
    public void bounceX() {
        velocityX = -velocityX * 0.8f;
    }

    public void bounceY() {
        velocityY = -velocityY * 0.8f;
    }
}