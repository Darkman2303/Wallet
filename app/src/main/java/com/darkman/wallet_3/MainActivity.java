package com.darkman.wallet_3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.darkman.wallet_3.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    SharedPreferences save_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        save_data = this.getSharedPreferences("save_data", Context.MODE_PRIVATE);
        int them = save_data.getInt("them", R.style.Theme_blue);
        setTheme(them);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, 0);
            return windowInsets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.navigation_accumulation, R.id.navigation_dashboard, R.id.navigation_setting)
                .build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(binding.navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.goalFragment) {
                binding.navView.setVisibility(View.GONE);
            } else {
                binding.navView.setVisibility(View.VISIBLE);
            }
        });
    }

}