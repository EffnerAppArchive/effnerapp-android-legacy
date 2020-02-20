package de.effnerapp.effner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;

import de.effnerapp.effner.data.DataStack;
import de.effnerapp.effner.data.dsbmobile.Vertretungen;
import de.effnerapp.effner.data.utils.DataStackReader;
import de.effnerapp.effner.data.utils.HeaderTextParser;
import de.effnerapp.effner.tools.LoginManager;
import de.effnerapp.effner.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;
    private static DataStack dataStack;
    private static Vertretungen vertretungen = new Vertretungen();

    public static DataStack getDataStack() {
        return dataStack;
    }

    public static Vertretungen getVertretungen() {
        return vertretungen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUp();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        new Thread(() -> {
            boolean registered;
            LoginManager loginManager = new LoginManager(this, this);
            if (sharedPreferences.getBoolean("APP_REGISTERED", false)) {
                registered = loginManager.login(sharedPreferences.getString("APP_AUTH_TOKEN", ""));
            } else {
                registered = false;
            }
            Log.d("SPLASH", "APP_REGISTERED: " + registered);
            if (registered) {
                loadData();
                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    if (bundle.getString("NOTIFICATION_CONTENT") != null) {
                        intent.putExtras(bundle);
                    }
                }

                startActivity(intent);
                finish();
            } else if (!loginManager.isError()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }).start();
    }

    private void startUp() {
        RelativeLayout layout = new RelativeLayout(this);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(300, 300);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageParams.setMargins(0, 775, 0, 0);
        layout.addView(imageView, imageParams);
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.setMargins(0, 1500, 0, 0);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(progressBar, params);
        setContentView(layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        runOnUiThread(() -> progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.icon_blue), PorterDuff.Mode.SRC_IN));
                        Thread.sleep(500);
                        runOnUiThread(() -> progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.icon_yellow), PorterDuff.Mode.SRC_IN));
                        Thread.sleep(500);
                        runOnUiThread(() -> progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.icon_green), PorterDuff.Mode.SRC_IN));
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
        }

    }

    private void loadData() {
        DataStackReader reader = new DataStackReader();
        dataStack = reader.read(sharedPreferences.getString("APP_USER_CLASS", ""), sharedPreferences.getString("APP_AUTH_TOKEN", ""));
        while (dataStack == null) {
            Log.d("SPLASH", "Loading Data...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HeaderTextParser parser = new HeaderTextParser();
        String result = null;
        try {
            result = parser.parse(dataStack.getHolidays(), sharedPreferences.getString("APP_USER_USERNAME", "") + "123");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("SPLASH", "HeaderText: " + result);

        //Authentication on DSB-SERVER
        boolean login = vertretungen.login(sharedPreferences.getString("APP_DSB_LOGIN_ID", ""), sharedPreferences.getString("APP_DSB_LOGIN_PASSWORD", ""));
        Log.d("SPLASH", "Login: " + login);
        try {
            vertretungen.load();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}