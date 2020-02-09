package de.effnerapp.effner;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    public static TextView pageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = SplashActivity.sharedPreferences;
        if(sharedPreferences.getBoolean("APP_DESIGN_DARK", false)) {
            Log.d("MAIN", "Nightmode: ON");
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            Log.d("MAIN", "Nightmode: OFF");
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        String sClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");
        if(sClass.startsWith("11") || sClass.startsWith("12")) {
            navView.getMenu().findItem(R.id.navigation_terms).setVisible(false);
        }

        pageTextView = findViewById(R.id.page_text);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.getString("NOTIFICATION_CONTENT") != null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setTitle(bundle.getString("NOTIFICATION_TITLE"))
                        .setMessage(bundle.getString("NOTIFICATION_CONTENT"))
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                dialog.show();
            }
        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

    }

}
