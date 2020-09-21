package de.effnerapp.effner;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import de.effnerapp.effner.data.dsbmobile.Substitutions;
import de.effnerapp.effner.tools.ClassUtils;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private Substitutions substitutions;

    public BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        SharedPreferences sharedPreferences = SplashActivity.sharedPreferences;
        if (sharedPreferences.getBoolean("APP_DESIGN_DARK", false)) {
            Log.d("MAIN", "Nightmode: ON");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            Log.d("MAIN", "Nightmode: OFF");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        NavigationUI.setupWithNavController(navView, navController);

        TextView pageHeader = findViewById(R.id.page_text);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> pageHeader.setText(destination.getLabel()));

        String sClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");
        if (ClassUtils.isAdvancedClass(sClass)) {
            navView.getMenu().findItem(R.id.navigation_terms).setVisible(false);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("NOTIFICATION_CONTENT") != null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setTitle(bundle.getString("NOTIFICATION_TITLE"))
                        .setMessage(bundle.getString("NOTIFICATION_CONTENT"))
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                dialog.show();

                bundle.remove("NOTIFICATION_TITLE");
                bundle.remove("NOTIFICATION_CONTENT");
            }
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Started");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        //Authentication on DSB-SERVER
        new Thread(() -> {
            substitutions = new Substitutions(sharedPreferences.getString("APP_DSB_LOGIN_ID", ""), sharedPreferences.getString("APP_DSB_LOGIN_PASSWORD", ""));
            //Load substitutions

            // TODO: remove sleep
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // TODO: check
            substitutions.load(() -> {
                System.out.println("Load finished!");
                if(Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.navigation_substitutions) {
                    runOnUiThread(() -> navController.navigate(R.id.navigation_substitutions));
                }
            });
        }).start();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        System.out.println("reenter");
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public Substitutions getSubstitutions() {
        return substitutions;
    }
}
