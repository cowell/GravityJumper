// C:/Users/user/AndroidStudioProjects/GravityJumper/app/src/main/java/com/example/gravityjumper/LevelTheme.java
package com.example.gravityjumper;

import android.graphics.Color;

public class LevelTheme {
    // Theme properties
    public final String themeName;
    public final int backgroundColor;
    public final int playerColor;
    public final int platformColor;
    public final int collectibleColor;
    public final int textColor;


    // Predefined themes
    // Define available themes
    public static final LevelTheme[] THEMES = {
            new LevelTheme("Classic", Color.BLACK, Color.WHITE, Color.GRAY, Color.YELLOW, Color.WHITE),
            new LevelTheme("Space", Color.rgb(0, 0, 50), Color.CYAN, Color.rgb(100, 100, 150), Color.YELLOW, Color.CYAN),
            new LevelTheme("Underwater", Color.rgb(0, 50, 100), Color.rgb(0, 255, 200), Color.rgb(0, 100, 150), Color.rgb(255, 215, 0), Color.WHITE),
            new LevelTheme("Lava", Color.rgb(50, 0, 0), Color.rgb(255, 100, 0), Color.rgb(100, 50, 0), Color.rgb(255, 255, 0), Color.rgb(255, 200, 0)),
            new LevelTheme("Forest", Color.rgb(0, 50, 0), Color.rgb(0, 200, 0), Color.rgb(100, 50, 0), Color.rgb(255, 0, 100), Color.rgb(200, 255, 200))
    };

    public LevelTheme(String themeName, int backgroundColor, int playerColor, int platformColor, int collectibleColor, int textColor) {
        this.themeName = themeName;
        this.backgroundColor = backgroundColor;
        this.playerColor = playerColor;
        this.platformColor = platformColor;
        this.collectibleColor = collectibleColor;
        this.textColor = textColor;
    }
    // Get a theme based on level number (cycles through available themes)
    public static LevelTheme getThemeForLevel(int levelNumber) {
        // Adjust level number to be 1-based index into themes array
        int themeIndex = (levelNumber - 1) % THEMES.length;
        return THEMES[themeIndex];
    }
    // Get all available themes
    public static LevelTheme[] getThemes() {
        return THEMES;
    }
}