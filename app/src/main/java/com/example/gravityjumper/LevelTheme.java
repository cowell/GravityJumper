// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/LevelTheme.java
package com.example.gravityjumper;

import android.graphics.Color;

public class LevelTheme {
    public final int backgroundColor;
    public final int platformColor;
    public final int playerColor;
    public final int collectibleColor;
    public final int textColor;
    public final String themeName;

    public LevelTheme(String themeName, int backgroundColor, int platformColor,
                      int playerColor, int collectibleColor, int textColor) {
        this.themeName = themeName;
        this.backgroundColor = backgroundColor;
        this.platformColor = platformColor;
        this.playerColor = playerColor;
        this.collectibleColor = collectibleColor;
        this.textColor = textColor;
    }

    // Predefined themes
    private static final LevelTheme[] THEMES = {
            // Classic theme
            new LevelTheme(
                    "Classic",
                    Color.BLACK,
                    Color.GRAY,
                    Color.BLUE,
                    Color.YELLOW,
                    Color.WHITE
            ),
            // Desert theme
            new LevelTheme(
                    "Desert",
                    Color.rgb(255, 222, 173), // Sandy background
                    Color.rgb(205, 133, 63),  // Brown platforms
                    Color.rgb(255, 140, 0),   // Orange player
                    Color.rgb(255, 215, 0),   // Gold collectibles
                    Color.rgb(139, 69, 19)    // Brown text
            ),
            // Ice theme
            new LevelTheme(
                    "Ice",
                    Color.rgb(220, 240, 255), // Ice blue background
                    Color.rgb(176, 196, 222), // Light blue platforms
                    Color.rgb(30, 144, 255),  // Dodger blue player
                    Color.rgb(255, 255, 255), // White collectibles
                    Color.rgb(25, 25, 112)    // Dark blue text
            ),
            // Forest theme
            new LevelTheme(
                    "Forest",
                    Color.rgb(34, 139, 34),   // Forest green background
                    Color.rgb(139, 69, 19),   // Brown platforms
                    Color.rgb(255, 99, 71),   // Tomato red player
                    Color.rgb(255, 215, 0),   // Gold collectibles
                    Color.WHITE               // White text
            ),
            // Space theme
            new LevelTheme(
                    "Space",
                    Color.rgb(25, 25, 112),   // Midnight blue background
                    Color.rgb(75, 0, 130),    // Indigo platforms
                    Color.rgb(0, 255, 255),   // Cyan player
                    Color.rgb(255, 255, 255), // White collectibles
                    Color.rgb(255, 255, 224)  // Light yellow text
            )
    };
    public static LevelTheme[] getThemes() {
        return THEMES;
    }
    public static LevelTheme getThemeForLevel(int level) {
        // Use modulo to cycle through themes
        return THEMES[(level - 1) % THEMES.length];
    }
}