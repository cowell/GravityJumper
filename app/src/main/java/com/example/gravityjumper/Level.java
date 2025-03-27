// Level.java
package com.example.gravityjumper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level {
    private final int levelNumber;
    private final List<Obstacle> obstacles;
    private final List<Collectible> collectibles;
    private RectF goalArea;
    private final int levelWidth;
    private final int levelHeight;
    private boolean completed = false;
    private final Context context;

    // New fields for scoring
    private int score = 0;

    // Add overloaded constructor
    public Level(int levelNumber, Context context) {
        this(levelNumber, context, 2000, 1500); // Default sizes
    }

    public Level(int levelNumber, Context context, int levelWidth, int levelHeight) {
        this.levelNumber = levelNumber;
        this.context = context;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        obstacles = new ArrayList<>();
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

    // Method to decrement score
    public void decrementScore(int amount) {
        score = Math.max(0, score - amount);
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

    // Getter for collectibles
    public List<Collectible> getCollectibles() {
        return collectibles;
    }

    // Getter for obstacles
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    private void generateLevel() {
        // Get current theme for this level
        LevelTheme theme = LevelTheme.getThemeForLevel(levelNumber);

        // Create obstacle bitmap from resource
        Bitmap obstacleBitmap = null;
        try {
            // Load the appropriate obstacle bitmap based on theme
            int obstacleResourceId = getObstacleResourceForTheme(theme.themeName);
            obstacleBitmap = BitmapFactory.decodeResource(context.getResources(), obstacleResourceId);
        } catch (Exception e) {
            // If bitmap loading fails, obstacles will be drawn with colors instead
        }

        // Add boundary walls as obstacles
        float speed = 0; // Stationary obstacles for boundaries

        // Top boundary
        obstacles.add(new Obstacle(0, 0, speed, obstacleBitmap) {
            @Override
            public boolean isColliding(float playerX, float playerY, int playerWidth, int playerHeight) {
                return new RectF(playerX, playerY, playerX + playerWidth, playerY + playerHeight)
                        .intersect(new RectF(getX(), getY(), getX() + getLevelWidth(), getY() + 50));
            }
        });

        // Bottom boundary
        obstacles.add(new Obstacle(0, levelHeight - 50, speed, obstacleBitmap) {
            @Override
            public boolean isColliding(float playerX, float playerY, int playerWidth, int playerHeight) {
                return new RectF(playerX, playerY, playerX + playerWidth, playerY + playerHeight)
                        .intersect(new RectF(getX(), getY(), getX() + getLevelWidth(), getY() + 50));
            }
        });

        // Left boundary
        obstacles.add(new Obstacle(0, 0, speed, obstacleBitmap) {
            @Override
            public boolean isColliding(float playerX, float playerY, int playerWidth, int playerHeight) {
                return new RectF(playerX, playerY, playerX + playerWidth, playerY + playerHeight)
                        .intersect(new RectF(getX(), getY(), getX() + 50, getY() + getLevelHeight()));
            }
        });

        // Right boundary
        obstacles.add(new Obstacle(levelWidth - 50, 0, speed, obstacleBitmap) {
            @Override
            public boolean isColliding(float playerX, float playerY, int playerWidth, int playerHeight) {
                return new RectF(playerX, playerY, playerX + playerWidth, playerY + playerHeight)
                        .intersect(new RectF(getX(), getY(), getX() + 50, getY() + getLevelHeight()));
            }
        });

        // Add obstacles based on level number (more obstacles for higher levels)
        Random random = new Random(levelNumber); // Use level as seed for consistent generation
        int obstacleCount = 5 + levelNumber;

        for (int i = 0; i < obstacleCount; i++) {
            // Make obstacles smaller to match their visual appearance
            int width = random.nextInt(200) + 100; // Reduced from 300
            int height = random.nextInt(20) + 20;  // Reduced from 30
            int x = random.nextInt(levelWidth - width - 200) + 100;
            int y = random.nextInt(levelHeight - height - 200) + 100;

            // Create a custom obstacle with specific dimensions
            final Obstacle obstacle = new Obstacle(x, y, 0, obstacleBitmap) {
                private final int customWidth = width;
                private final int customHeight = height;

                @Override
                public int getWidth() {
                    return customWidth;
                }

                @Override
                public int getHeight() {
                    return customHeight;
                }

                @Override
                public boolean isColliding(float playerX, float playerY, int playerWidth, int playerHeight) {
                    // For the floating platform image, adjust the collision box to match the visible part
                    // These values need to be tuned based on your specific obstacle image
                    float collisionX = getX() + customWidth * 0.1f;  // 10% inset from left
                    float collisionY = getY() + customHeight * 0.2f; // 20% inset from top
                    float collisionWidth = customWidth * 0.8f;       // 80% of original width
                    float collisionHeight = customHeight * 0.6f;     // 60% of original height

                    return new RectF(playerX, playerY, playerX + playerWidth, playerY + playerHeight)
                            .intersect(new RectF(collisionX, collisionY,
                                    collisionX + collisionWidth,
                                    collisionY + collisionHeight));
                }
            };

            obstacles.add(obstacle);
        }

        // Add collectibles with a safe distance from obstacles
        int collectibleCount = 3;
        int safeDistance = 120; // Increase minimum distance from obstacles

        for (int i = 0; i < collectibleCount; i++) {
            int x = random.nextInt(levelWidth - 300) + 150; // More padding from walls
            int y = random.nextInt(levelHeight - 300) + 150; // More padding from walls

            // Check if too close to any obstacle and reposition if needed
            boolean validPosition = false;
            int attempts = 0;

            while (!validPosition && attempts < 15) {
                validPosition = true;

                for (Obstacle obstacle : obstacles) {
                    RectF rect = new RectF(obstacle.getX(), obstacle.getY(),
                            obstacle.getX() + obstacle.getWidth(),
                            obstacle.getY() + obstacle.getHeight());

                    // Check if collectible is too close to this obstacle
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

            // Check if too close to any obstacle
            for (Obstacle obstacle : obstacles) {
                RectF rect = new RectF(obstacle.getX(), obstacle.getY(),
                        obstacle.getX() + obstacle.getWidth(),
                        obstacle.getY() + obstacle.getHeight());

                if (goalX < rect.right + goalSafeDistance && goalX + goalSize > rect.left - goalSafeDistance &&
                        goalY < rect.bottom + goalSafeDistance && goalY + goalSize > rect.top - goalSafeDistance) {
                    validGoalPosition = false;
                    break;
                }
            }

            // Check if too close to any collectible
            for (Collectible collectible : collectibles) {
                if (Math.hypot(collectible.getX() - (goalX + (double) goalSize /2),
                        collectible.getY() - (goalY + (double) goalSize /2)) < goalSafeDistance) {
                    validGoalPosition = false;
                    break;
                }
            }

            attempts++;
        }

        goalArea = new RectF(goalX, goalY, goalX + goalSize, goalY + goalSize);
    }

    // Helper method to get the correct obstacle resource based on theme name
    private int getObstacleResourceForTheme(String themeName) {
        switch(themeName) {
            case "Classic":
                return R.drawable.obstacle_classic;
            case "Space":
                return R.drawable.obstacle_space;
            case "Underwater":
                return R.drawable.obstacle_underwater;
            case "Lava":
                return R.drawable.obstacle_lava;
            case "Forest":
                return R.drawable.obstacle_forest;
            default:
                return R.drawable.obstacle_classic;
        }
    }

    public void draw(Canvas canvas, Paint paint, LevelTheme theme) {
        // Save original paint properties
        int originalColor = paint.getColor();
        int originalAlpha = paint.getAlpha();

        // Draw obstacles with theme color
        paint.setColor(theme.platformColor);
        for (Obstacle obstacle : obstacles) {
            canvas.drawRect(new RectF(obstacle.getX(), obstacle.getY(),
                    obstacle.getX() + obstacle.getWidth(),
                    obstacle.getY() + obstacle.getHeight()), paint);
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
        int goalColor = blendColors(theme.playerColor);
        paint.setColor(goalColor);
        canvas.drawRect(goalArea, paint);

        // Restore original paint properties
        paint.setColor(originalColor);
        paint.setAlpha(originalAlpha);
    }

    // Helper method to blend colors
    private int blendColors(int color1) {
        final float inverseRatio = 1f - (float) 0.5;
        float r = (Color.red(color1) * (float) 0.5) + (Color.red(Color.GREEN) * inverseRatio);
        float g = (Color.green(color1) * (float) 0.5) + (Color.green(Color.GREEN) * inverseRatio);
        float b = (Color.blue(color1) * (float) 0.5) + (Color.blue(Color.GREEN) * inverseRatio);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public void checkCollisions(Player player) {
        // First check level boundaries
        float playerRight = player.getX() + player.getWidth();
        float playerBottom = player.getY() + player.getHeight();

        // Left boundary
        if (player.getX() < 0) {
            player.setX(0);
            player.bounceX();
        }

        // Right boundary
        if (playerRight > levelWidth) {
            player.setX(levelWidth - player.getWidth());
            player.bounceX();
        }

        // Top boundary
        if (player.getY() < 0) {
            player.setY(0);
            player.bounceY();
        }

        // Bottom boundary
        if (playerBottom > levelHeight) {
            player.setY(levelHeight - player.getHeight());
            player.bounceY();
        }

        // Check obstacle collisions
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isColliding(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                resolveCollision(obstacle, player);
            }
        }

        // Collectibles are just pickups, not obstacles
        for (Collectible collectible : collectibles) {
            if (collectible.isNotCollected()) {
                float distX = collectible.getX() - (player.getX() + (float) player.getWidth() /2);
                float distY = collectible.getY() - (player.getY() + (float) player.getHeight() /2);
                float distance = (float) Math.sqrt(distX * distX + distY * distY);

                if (distance < 80) { // Large collection radius
                    collectible.collect();
                    // Base points for collecting an item
                    int collectibleValue = 100;
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
                // Bonus for completing the level
                int levelCompletionBonus = 500;
                score += levelCompletionBonus;
                // Apply level multiplier to make higher levels worth more
                score += levelCompletionBonus * levelNumber;
                SoundManager.getInstance(context).playLevelCompleteSound();
            }
        }
    }

    private void resolveCollision(Obstacle obstacle, Player player) {
        // Get the bounds of both objects
        RectF playerBounds = new RectF(player.getX(), player.getY(),
                player.getX() + player.getWidth(),
                player.getY() + player.getHeight());

        // For the obstacle, use the adjusted collision bounds
        float collisionX = obstacle.getX() + obstacle.getWidth() * 0.1f;
        float collisionY = obstacle.getY() + obstacle.getHeight() * 0.2f;
        float collisionWidth = obstacle.getWidth() * 0.8f;
        float collisionHeight = obstacle.getHeight() * 0.6f;

        RectF obstacleBounds = new RectF(collisionX, collisionY,
                collisionX + collisionWidth,
                collisionY + collisionHeight);

        // Calculate the overlap in each direction
        float overlapLeft = playerBounds.right - obstacleBounds.left;
        float overlapRight = obstacleBounds.right - playerBounds.left;
        float overlapTop = playerBounds.bottom - obstacleBounds.top;
        float overlapBottom = obstacleBounds.bottom - playerBounds.top;

        // Determine which side has the smallest overlap
        boolean fromLeft = overlapLeft < overlapRight;
        boolean fromTop = overlapTop < overlapBottom;

        float minXOverlap = Math.min(overlapLeft, overlapRight);
        float minYOverlap = Math.min(overlapTop, overlapBottom);

        // Determine if collision is more horizontal or vertical
        if (minXOverlap < minYOverlap) {
            // Horizontal collision
            if (fromLeft) {
                // Collision from left side of obstacle
                player.setX(obstacleBounds.left - player.getWidth());
            } else {
                // Collision from right side of obstacle
                player.setX(obstacleBounds.right);
            }
            player.bounceX();
        } else {
            // Vertical collision
            if (fromTop) {
                // Collision from top of obstacle (player is above)
                player.setY(obstacleBounds.top - player.getHeight());
                // Only bounce if moving downward
                if (player.getVelocityY() > 0) {
                    player.bounceY();
                } else {
                    player.setVelocityY(0); // Just stop if moving up
                }
            } else {
                // Collision from bottom of obstacle (player is below)
                player.setY(obstacleBounds.bottom);
                player.bounceY();
            }
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    // Inner class for Collectible (kept from original)
    public static class Collectible {
        private final float x;
        private final float y;
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