package de.effnerapp.effner;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.effnerapp.effner.data.utils.ApiClient;
import de.effnerapp.effner.tools.ClassUtils;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private long activityCreatedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        activityCreatedTime = System.currentTimeMillis();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setDarkMode(sharedPreferences.getBoolean("APP_DESIGN_DARK", false));

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

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
    protected void onPostResume() {
        super.onPostResume();
        if (System.currentTimeMillis() - activityCreatedTime >= TimeUnit.MINUTES.toMillis(0)) {
            reloadData();
        }
    }

    private void reloadData() {
        ApiClient.getInstance().loadData((isSuccess, data) -> {
            if (isSuccess && data.getStatus().isLogin()) {
                // success
                runOnUiThread(() -> Toast.makeText(this, "Daten wurden aktualisiert.", Toast.LENGTH_SHORT).show());
            } else {
                // TODO: indicate error (snackbar?)
                if (!isSuccess) {
                    runOnUiThread(() -> Snackbar.make(findViewById(R.id.root), "Verbindung mit dem Server fehlgeschlagen.", BaseTransientBottomBar.LENGTH_LONG).setAction("Retry", v -> reloadData()).show());
                } else if (data.getStatus().getMsg().equals("AUTHENTICATION_FAILED")) {
                    runOnUiThread(() -> Snackbar.make(findViewById(R.id.root), "Authentifizierung mit dem Server fehlgeschlagen.", BaseTransientBottomBar.LENGTH_LONG).setAction("Retry", v -> reloadData()).show());
                } else {
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                }
            }
        });

        // TODO: reload substitutions
//            // load substitutions
//            substitutions = new Substitutions(sharedPreferences.getString("APP_DSB_LOGIN_ID", ""), sharedPreferences.getString("APP_DSB_LOGIN_PASSWORD", ""));
//            new Thread(() -> substitutions.load(() -> {
//                Log.d("Splash", "DSB data load finished!");
//                if (SubstitutionsFragment.getInstance() != null && SubstitutionsFragment.getInstance().isVisible()) {
//                    Log.d("Splash", "Substitution fragment currently visible, notifying due to data load finished.");
//                    runOnUiThread(() -> SubstitutionsFragment.getInstance().onDataLoadFinished());
//                }
//            })).start();
    }

    public static MainActivity getInstance() {
        return instance;
    }

}
