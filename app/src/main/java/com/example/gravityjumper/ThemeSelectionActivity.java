// ThemeSelectionActivity.java
package com.example.gravityjumper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.ArrayList;
import java.util.List;

public class ThemeSelectionActivity extends AppCompatActivity {
    private ListView themeListView;
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String CURRENT_THEME_KEY = "CurrentTheme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_selection);

        // Hide system UI for immersive experience
        hideSystemUI();

        themeListView = findViewById(R.id.theme_list_view);

        // Populate list with theme names
        List<String> themeNames = new ArrayList<>();
        for (LevelTheme theme : LevelTheme.THEMES) {
            themeNames.add(theme.themeName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, themeNames);
        themeListView.setAdapter(adapter);

        // Handle theme selection
        themeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTheme = themeNames.get(position);
                saveSelectedTheme(selectedTheme);
                finish(); // Return to previous screen
            }
        });
    }

    private void saveSelectedTheme(String themeName) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_THEME_KEY, themeName);
        editor.apply();
    }

    private void hideSystemUI() {
        // Use WindowCompat instead of the deprecated setDecorFitsSystemWindows
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }
}