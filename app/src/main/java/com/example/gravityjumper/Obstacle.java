// Obstacle.java
package com.example.gravityjumper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Obstacle {
    private float x;
    private float y;
    private float speed;
    private Bitmap bitmap;
    private int width;
    private int height;

    public Obstacle(float x, float y, float speed, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.bitmap = bitmap;

        if (bitmap != null) {
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();
        } else {
            // Default size if bitmap is null
            this.width = 100;
            this.height = 30;
        }
    }

    public void update() {
        // Move the obstacle (typically from right to left in a side-scroller)
        x -= speed;
    }

    public void draw(Canvas canvas) {
        if (bitmap != null && canvas != null) {
            canvas.drawBitmap(bitmap, x, y, null);
        } else if (canvas != null) {
            // Draw a rectangle if bitmap is null
            Paint paint = new Paint();
            paint.setARGB(255, 100, 100, 100);
            canvas.drawRect(x, y, x + width, y + height, paint);
        }
    }

    // Collision detection

    public boolean isColliding(float playerX, float playerY, int playerWidth, int playerHeight) {
        // Create rectangles for collision detection
        RectF obstacleRect = new RectF(x, y, x + width, y + height);
        RectF playerRect = new RectF(playerX, playerY, playerX + playerWidth, playerY + playerHeight);

        // Check if rectangles intersect
        return RectF.intersects(obstacleRect, playerRect);
    }

    // Check if obstacle is off screen and can be recycled
    public boolean isOffScreen() {
        return x + width < 0;
    }

    // Getters and setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (bitmap != null) {
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();
        }
    }
}