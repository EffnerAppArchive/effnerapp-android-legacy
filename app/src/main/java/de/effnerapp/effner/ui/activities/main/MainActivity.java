package de.effnerapp.effner.ui.activities.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.dsbmobile.DSBClient;
import de.effnerapp.effner.data.utils.ApiClient;
import de.effnerapp.effner.tools.ClassUtils;
import de.effnerapp.effner.ui.activities.splash.SplashActivity;
import de.effnerapp.effner.ui.fragments.substitutions.SubstitutionsFragment;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private long activityCreatedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Called MainActivity#onCreate()");
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
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            pageHeader.setText(destination.getLabel());

            // show labels and set home fragment checkable if we navigate back from the news fragment
            if (destination.getId() != R.id.navigation_news && destination.getId() != R.id.navigation_information) {
                navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
                navView.getMenu().getItem(0).setCheckable(true);
            }
        });

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
        // AppCompatDelegate.setDefaultNightMode(); causes also the finished activities to recreate which getDelegate().setLocalNightMode() doesn't.
        getDelegate().setLocalNightMode(enable ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (System.currentTimeMillis() - activityCreatedTime >= TimeUnit.MINUTES.toMillis(10)) {
            activityCreatedTime = System.currentTimeMillis();
            reloadData();
        }
    }

    private void reloadData() {
        System.out.println("call reloaddata");
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
                    System.err.println("startign splash");
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                }
            }
        });

        // TODO: handle success/error messages?
        new Thread(() -> DSBClient.getInstance().load(() -> {
            Log.d("Main", "DSB data load finished!");
            if (SubstitutionsFragment.getInstance() != null && SubstitutionsFragment.getInstance().isVisible()) {
                Log.d("Main", "Substitution fragment currently visible, notifying due to data load finished.");
                runOnUiThread(() -> SubstitutionsFragment.getInstance().onDataLoadFinished());
            }
        })).start();
    }

    public static MainActivity getInstance() {
        return instance;
    }

}
