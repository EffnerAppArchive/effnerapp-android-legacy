package de.effnerapp.effner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Timer;
import java.util.TimerTask;

import de.effnerapp.effner.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    public static TextView pageTextView;
    public static boolean eaTriggered = false;
    private int duration;

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_substitutions, R.id.navigation_schooltests,
                R.id.navigation_terms, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        pageTextView = findViewById(R.id.page_text);

        pageTextView.setOnLongClickListener(view -> {
            eaTriggered = true;
            duration = 0;
            new Thread(() -> {
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        duration++;
                        if(duration >= 10) {
                            eaTriggered = false;
                            timer.cancel();
                        }
                    }
                }, 1000, 1000);
            }).start();
            return true;
        });

    }

}
