package com.example.yokailist.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.yokailist.R;
import com.example.yokailist.databinding.ActivityMainBinding;
import com.example.yokailist.fragments.DiscoverFragment;
import com.example.yokailist.fragments.HomeFragment;
import com.example.yokailist.fragments.MyListFragment;
import com.example.yokailist.fragments.SeasonalFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String KEY_SELECTED_NAV_ITEM = "selected_nav_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadThemePreference();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        boolean isDarkMode = getSharedPreferences("theme_pref", MODE_PRIVATE).getBoolean("dark_mode", false);
        if (isDarkMode) {
            binding.ibSwitch.setImageResource(R.drawable.ic_dark);
        } else {
            binding.ibSwitch.setImageResource(R.drawable.ic_light);
        }


        binding.ibSwitch.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveThemePreference(false);
                binding.ibSwitch.setImageResource(R.drawable.ic_light);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveThemePreference(true);
                binding.ibSwitch.setImageResource(R.drawable.ic_dark);

            }

            recreate();
        });


        binding.vTopDividerLine.setVisibility(View.VISIBLE);
        replaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.nb_home) {
                Fragment homeFragment = new HomeFragment();
                selectedFragment = homeFragment;
            } else if (id == R.id.nb_discover) {
                Fragment discoverFragment = new DiscoverFragment();
                selectedFragment = discoverFragment;
            } else if (id == R.id.nb_seasonal) {
                binding.vTopDividerLine.setVisibility(View.GONE);
                Fragment seasonalFragment = new SeasonalFragment();
                selectedFragment = seasonalFragment;
            } else if (id == R.id.nb_myList) {
                Fragment myListFragment = new MyListFragment();
                selectedFragment = myListFragment;
            }
            return replaceFragment(selectedFragment);
        });

        if (savedInstanceState != null) {
            int selectedItemId = savedInstanceState.getInt(KEY_SELECTED_NAV_ITEM, R.id.nb_home);
            binding.bottomNavigation.setSelectedItemId(selectedItemId);
        } else {
            binding.vTopDividerLine.setVisibility(View.VISIBLE);
            replaceFragment(new HomeFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.nb_home);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_NAV_ITEM, binding.bottomNavigation.getSelectedItemId());
    }

    private boolean replaceFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void saveThemePreference(boolean isDarkMode) {
        SharedPreferences sharedPref = getSharedPreferences("theme_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("dark_mode", isDarkMode);
        editor.apply();
    }

    private void loadThemePreference() {
        SharedPreferences sharedPref = getSharedPreferences("theme_pref", MODE_PRIVATE);
        boolean isDarkMode = sharedPref.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}