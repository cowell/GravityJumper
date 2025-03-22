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

    // New fields for scoring
    private int score = 0;
    private int collectibleValue = 100; // Base points for collecting an item
    private int levelCompletionBonus = 500; // Bonus for completing the level

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

    // Getters for score and level information
    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
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
            int x = random.nextInt(levelWidth - width - 200) + 100; // More padding
            int y = random.nextInt(levelHeight - height - 200) + 100; // More padding

            platforms.add(new Platform(x, y, width, height));
        }

        // Add collectibles with a safe distance from platforms
        int collectibleCount = 3;
        int safeDistance = 120; // Increase minimum distance from platforms

        for (int i = 0; i < collectibleCount; i++) {
            int x = random.nextInt(levelWidth - 300) + 150; // More padding from walls
            int y = random.nextInt(levelHeight - 300) + 150; // More padding from walls

            // Check if too close to any platform and reposition if needed
            boolean validPosition = false;
            int attempts = 0;

            while (!validPosition && attempts < 15) {
                validPosition = true;

                for (Platform platform : platforms) {
                    RectF rect = platform.getRect();

                    // Check if collectible is too close to this platform
                    if (x < rect.right + safeDistance && x > rect.left - safeDistance &&
                            y < rect.bottom + safeDistance && y > rect.top - safeDistance) {
                        // Too close, mark as invalid position
                        validPosition = false;

                        // Try new position
                        x = random.nextInt(levelWidth - 300) + 150;
                        y = random.nextInt(levelHeight - 300) + 150;
                        break;
                    }
                }

                attempts++;
            }

            collectibles.add(new Collectible(x, y));
        }

        // Set goal area with safe distance
        boolean validGoalPosition = false;
        int goalX = 0, goalY = 0;
        int goalSize = 100;
        int goalSafeDistance = 150;
        int attempts = 0;

        while (!validGoalPosition && attempts < 20) {
            validGoalPosition = true;
            goalX = random.nextInt(levelWidth - 300) + 150;
            goalY = random.nextInt(levelHeight - 300) + 150;

            // Check if too close to any platform
            for (Platform platform : platforms) {
                RectF rect = platform.getRect();
                if (goalX < rect.right + goalSafeDistance && goalX + goalSize > rect.left - goalSafeDistance &&
                        goalY < rect.bottom + goalSafeDistance && goalY + goalSize > rect.top - goalSafeDistance) {
                    validGoalPosition = false;
                    break;
                }
            }

            // Check if too close to any collectible
            for (Collectible collectible : collectibles) {
                if (Math.hypot(collectible.getX() - (goalX + goalSize/2),
                        collectible.getY() - (goalY + goalSize/2)) < goalSafeDistance) {
                    validGoalPosition = false;
                    break;
                }
            }

            attempts++;
        }

        goalArea = new RectF(goalX, goalY, goalX + goalSize, goalY + goalSize);
    }

    public void draw(Canvas canvas, Paint paint, LevelTheme theme) {
        // Save original paint properties
        int originalColor = paint.getColor();
        int originalAlpha = paint.getAlpha();

        // Draw platforms with theme color
        paint.setColor(theme.platformColor);
        for (Platform platform : platforms) {
            canvas.drawRect(platform.getRect(), paint);
        }

        // Draw collectibles - larger and with glow effect
        paint.setColor(theme.collectibleColor);
        for (Collectible collectible : collectibles) {
            if (collectible.isNotCollected()) {
                // Add a pulsing glow effect (draw this first, behind the main circle)
                paint.setAlpha(100);
                canvas.drawCircle(collectible.getX(), collectible.getY(), 40, paint);
                paint.setAlpha(255);

                // Then draw the main collectible
                canvas.drawCircle(collectible.getX(), collectible.getY(), 25, paint);
            }
        }

        // Derive goal color from theme (green tint of player color)
        int goalColor = blendColors(theme.playerColor, Color.GREEN, 0.5f);
        paint.setColor(goalColor);
        canvas.drawRect(goalArea, paint);

        // Restore original paint properties
        paint.setColor(originalColor);
        paint.setAlpha(originalAlpha);
    }

    // Helper method to blend colors
    private int blendColors(int color1, int color2, float ratio) {
        final float inverseRatio = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRatio);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRatio);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRatio);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public void checkCollisions(Player player) {
        // ONLY check platform collisions for obstacle collision
        for (Platform platform : platforms) {
            if (platform.intersects(player)) {
                resolveCollision(platform, player);
            }
        }

        // Collectibles are just pickups, not obstacles
        for (Collectible collectible : collectibles) {
            if (collectible.isNotCollected()) {
                float distX = collectible.getX() - (player.getX() + player.getWidth()/2);
                float distY = collectible.getY() - (player.getY() + player.getHeight()/2);
                float distance = (float) Math.sqrt(distX * distX + distY * distY);

                if (distance < 80) { // Large collection radius
                    collectible.collect();
                    score += collectibleValue;
                    SoundManager.getInstance(context).playCollectSound();
                }
            }
        }

        // Check goal area and award completion bonus
        RectF playerRect = new RectF(player.getX(), player.getY(),
                player.getX() + player.getWidth(),
                player.getY() + player.getHeight());
        if (RectF.intersects(playerRect, goalArea)) {
            // All collectibles must be collected to complete level
            boolean allCollected = true;
            for (Collectible c : collectibles) {
                if (c.isNotCollected()) {
                    allCollected = false;
                    break;
                }
            }

            if (allCollected && !completed) {
                completed = true;
                // Award bonus points for completing the level
                score += levelCompletionBonus;
                // Apply level multiplier to make higher levels worth more
                score += levelCompletionBonus * levelNumber;
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
        public float getRadius() { return 25; } // Add this method for consistency

        public boolean isCollected() {
            return collected;
        }

        // Add this method to avoid negating isCollected() everywhere
        public boolean isNotCollected() {
            return !collected;
        }

        public void collect() {
            collected = true;
        }
    }
}