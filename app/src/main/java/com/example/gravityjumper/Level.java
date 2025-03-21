// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/Level.java

package com.example.gravityjumper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level {
    private int levelNumber;
    private List<Platform> platforms;
    private List<Collectible> collectibles;
    private RectF goalArea;
    private int levelWidth, levelHeight;
    private boolean completed = false;
    private Context context;

    // Add overloaded constructor
    public Level(int levelNumber, Context context) {
        this(levelNumber, context, 2000, 1500); // Default sizes
    }

    public Level(int levelNumber, Context context, int levelWidth, int levelHeight) {
        this.levelNumber = levelNumber;
        this.context = context;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        platforms = new ArrayList<>();
        collectibles = new ArrayList<>();

        generateLevel();
    }

    // This method returns the level number
    public int getLevelNumber() {
        return levelNumber;
    }

    // Getters for level dimensions
    public int getLevelWidth() {
        return levelWidth;
    }

    public int getLevelHeight() {
        return levelHeight;
    }

    private void generateLevel() {
        // Add boundary walls
        platforms.add(new Platform(0, 0, levelWidth, 50)); // Top
        platforms.add(new Platform(0, levelHeight - 50, levelWidth, 50)); // Bottom
        platforms.add(new Platform(0, 0, 50, levelHeight)); // Left
        platforms.add(new Platform(levelWidth - 50, 0, 50, levelHeight)); // Right

        // Add platforms based on level number (more platforms for higher levels)
        Random random = new Random(levelNumber); // Use level as seed for consistent generation
        int platformCount = 5 + levelNumber;

        for (int i = 0; i < platformCount; i++) {
            int width = random.nextInt(300) + 100;
            int height = random.nextInt(30) + 20;
            int x = random.nextInt(levelWidth - width - 100) + 50;
            int y = random.nextInt(levelHeight - height - 100) + 50;

            platforms.add(new Platform(x, y, width, height));
        }

        // Add collectibles
        int collectibleCount = 3;
        for (int i = 0; i < collectibleCount; i++) {
            int x = random.nextInt(levelWidth - 100) + 50;
            int y = random.nextInt(levelHeight - 100) + 50;

            collectibles.add(new Collectible(x, y));
        }

        // Set goal area
        int goalX = random.nextInt(levelWidth - 200) + 100;
        int goalY = random.nextInt(levelHeight - 200) + 100;
        goalArea = new RectF(goalX, goalY, goalX + 100, goalY + 100);
    }

    public void draw(Canvas canvas, Paint paint) {
        // Draw platforms
        paint.setColor(Color.GRAY);
        for (Platform platform : platforms) {
            canvas.drawRect(platform.getRect(), paint);
        }

        // Draw collectibles
        paint.setColor(Color.YELLOW);
        for (Collectible collectible : collectibles) {
            if (!collectible.isCollected()) {
                canvas.drawCircle(collectible.getX(), collectible.getY(), 15, paint);
            }
        }

        // Draw goal area
        paint.setColor(Color.GREEN);
        canvas.drawRect(goalArea, paint);
    }

    public void checkCollisions(Player player) {
        // Check platform collisions
        for (Platform platform : platforms) {
            if (platform.intersects(player)) {
                resolveCollision(platform, player);
            }
        }

        // Check collectible collisions
        for (Collectible collectible : collectibles) {
            if (!collectible.isCollected() &&
                    Math.hypot(collectible.getX() - player.getX() - player.getWidth()/2,
                            collectible.getY() - player.getY() - player.getHeight()/2) < 30) {
                collectible.collect();
                SoundManager.getInstance(context).playCollectSound();
            }
        }

        // Check goal area
        RectF playerRect = new RectF(player.getX(), player.getY(),
                player.getX() + player.getWidth(),
                player.getY() + player.getHeight());
        if (RectF.intersects(playerRect, goalArea)) {
            // All collectibles must be collected to complete level
            boolean allCollected = true;
            for (Collectible c : collectibles) {
                if (!c.isCollected()) {
                    allCollected = false;
                    break;
                }
            }

            if (allCollected) {
                completed = true;
                SoundManager.getInstance(context).playLevelCompleteSound();
            }
        }
    }

    private void resolveCollision(Platform platform, Player player) {
        float playerRight = player.getX() + player.getWidth();
        float playerBottom = player.getY() + player.getHeight();
        float platformRight = platform.getRect().right;
        float platformBottom = platform.getRect().bottom;

        // Calculate overlap on each side
        float leftOverlap = playerRight - platform.getRect().left;
        float rightOverlap = platformRight - player.getX();
        float topOverlap = playerBottom - platform.getRect().top;
        float bottomOverlap = platformBottom - player.getY();

        // Find the smallest overlap
        float minOverlap = Math.min(Math.min(leftOverlap, rightOverlap), Math.min(topOverlap, bottomOverlap));

        // Resolve based on smallest overlap
        if (minOverlap == leftOverlap) {
            player.setX(player.getX() - leftOverlap);
            player.bounceX();
        } else if (minOverlap == rightOverlap) {
            player.setX(player.getX() + rightOverlap);
            player.bounceX();
        } else if (minOverlap == topOverlap) {
            player.setY(player.getY() - topOverlap);
            player.bounceY();
        } else if (minOverlap == bottomOverlap) {
            player.setY(player.getY() + bottomOverlap);
            player.bounceY();
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    // Inner classes
    public static class Platform {
        private RectF rect;

        public Platform(float left, float top, float width, float height) {
            rect = new RectF(left, top, left + width, top + height);
        }

        public RectF getRect() {
            return rect;
        }

        public boolean intersects(Player player) {
            RectF playerRect = new RectF(player.getX(), player.getY(),
                    player.getX() + player.getWidth(),
                    player.getY() + player.getHeight());
            return RectF.intersects(rect, playerRect);
        }
    }

    public static class Collectible {
        private float x, y;
        private boolean collected = false;

        public Collectible(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() { return x; }
        public float getY() { return y; }

        public boolean isCollected() {
            return collected;
        }

        public void collect() {
            collected = true;
        }
    }
}