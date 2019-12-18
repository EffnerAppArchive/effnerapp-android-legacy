package de.effnerapp.effner;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUp();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean registered;
        if(sharedPreferences.getBoolean("APP_REGISTERED", false)) {
            LoginManager loginManager = new LoginManager();
            if(loginManager.login(sharedPreferences.getString("APP_AUTH_TOKEN", ""))) {
                registered = true;
            } else {
                registered = false;
            }
        } else {
            registered = false;
        }
        Log.d("SPLASH", "APP_REGISTERED: " + registered);
        if(registered) {
            loadData();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, "Bitte melde dich an!", Toast.LENGTH_LONG).show();
        }
        finish();
    }


    private void startUp() {
        RelativeLayout layout = new RelativeLayout(this);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(300,300);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageParams.setMargins(0,775,0,0);
        layout.addView(imageView, imageParams);
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.setMargins(0,1500,0,0);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(progressBar, params);
        setContentView(layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.icon_blue), PorterDuff.Mode.SRC_IN);
                        Thread.sleep(500);
                        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.icon_yellow), PorterDuff.Mode.SRC_IN);
                        Thread.sleep(500);
                        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.icon_green), PorterDuff.Mode.SRC_IN);
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

        HeaderTextParser parser = new HeaderTextParser();
        String result = null;
        try {
            result = parser.parse(dataStack.getHolidays());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("SPLASH", "HeaderText: " + result);

        Log.d("SPLASH", "------------");
        boolean login = vertretungen.login("***REMOVED***", "***REMOVED***");
        Log.d("SPLASH", "Res: " + login);
        try {
            vertretungen.load();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static DataStack getDataStack() {
        return dataStack;
    }

    public static Vertretungen getVertretungen() {
        return vertretungen;
    }
}