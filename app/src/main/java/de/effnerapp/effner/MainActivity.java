package de.effnerapp.effner;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import de.effnerapp.effner.tools.ClassUtils;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;

    public BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setDarkMode(sharedPreferences.getBoolean("APP_DESIGN_DARK", false));

        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);

        // setup navView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        NavigationUI.setupWithNavController(navView, navController);

        TextView pageHeader = findViewById(R.id.page_text);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> pageHeader.setText(destination.getLabel()));

        String sClass = sharedPreferences.getString("APP_USER_CLASS", "");
        if (ClassUtils.isAdvancedClass(sClass)) {
            navView.getMenu().findItem(R.id.navigation_terms).setVisible(false);
        }

        // cancel all notifications
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

    }

    private void setDarkMode(boolean enable) {
        AppCompatDelegate.setDefaultNightMode(enable ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        System.out.println("reenter");
    }

    public static MainActivity getInstance() {
        return instance;
    }

}
