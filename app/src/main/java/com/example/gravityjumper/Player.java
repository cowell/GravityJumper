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
    private int width = 50;  // Default size if bitmap fails to load
    private int height = 50;
    private RectF boundingBox;

    public Player(Context context) {
        try {
            // Debug log to check resource loading
            Log.d("Player", "Attempting to load player bitmap");

            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);

            if (bitmap != null) {
                width = bitmap.getWidth();
                height = bitmap.getHeight();
                Log.d("Player", "Bitmap loaded successfully: " + width + "x" + height);
            } else {
                Log.e("Player", "Failed to load bitmap - returned null");
            }
        } catch (Exception e) {
            Log.e("Player", "Failed to load player bitmap: " + e.getMessage());
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
            canvas.drawBitmap(bitmap, x, y, paint);
        } else {
            // Fallback to a simple colored rectangle if bitmap fails to load
            paint.setColor(Color.RED);
            canvas.drawRect(boundingBox, paint);

            // Debug drawing for bounding box
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            canvas.drawRect(boundingBox, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    // Getters and setters for collision detection
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
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public RectF getBoundingBox() { return boundingBox; }

    public void bounceX() {
        velocityX = -velocityX * 0.8f;
    }

    public void bounceY() {
        velocityY = -velocityY * 0.8f;
    }
}