package de.effnerapp.effner;

import android.accounts.Account;
import android.accounts.AccountManager;
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

import de.effnerapp.effner.data.DataStack;
import de.effnerapp.effner.data.dsbmobile.Substitutions;
import de.effnerapp.effner.data.utils.DataStackReader;
import de.effnerapp.effner.services.Authenticator;
import de.effnerapp.effner.tools.error.ErrorUtils;
import de.effnerapp.effner.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;
    private static DataStack dataStack;
    private static Substitutions substitutions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUp();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        AccountManager accountManager = AccountManager.get(this);

        if (sharedPreferences.getBoolean("APP_REGISTERED", false)) {
            if (accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE).length > 0) {
                new Thread(() -> {
                    DataStackReader reader = new DataStackReader(this, this);
                    dataStack = reader.read(sharedPreferences.getString("APP_USER_CLASS", ""), accountManager.getPassword(accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)[0]));
                    if(dataStack != null && dataStack.getStatus() != null && dataStack.getStatus().isLogin()) {
                        sharedPreferences.edit().putString("APP_USER_USERNAME", dataStack.getUsername()).apply();
                        loadData();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        if(dataStack == null || dataStack.getStatus() == null) {
                            new ErrorUtils(this, this).showError("Could not connect to server.", false);
                        } else if(dataStack.getStatus().getMsg().equals("AUTHENTICATION_FAILED")) {
                            startActivity(new Intent(this, LoginActivity.class));
                        } else {
                            new ErrorUtils(this, this).showError(dataStack.getStatus().getMsg(), false);
                        }

                    }
                }).start();
            } else {
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        } else {
            // REMOVE all accounts!
            for (Account account : accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)) {
                accountManager.removeAccountExplicitly(account);
            }
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
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

    public void loadData() {
        //Authentication on DSB-SERVER
        new Thread(() -> {
            substitutions = new Substitutions(sharedPreferences.getString("APP_DSB_LOGIN_ID", ""), sharedPreferences.getString("APP_DSB_LOGIN_PASSWORD", ""));
            boolean login = substitutions.login();
            Log.d("SPLASH", "DSB-Auth: " + login);
            //Load substitutions
            substitutions.load();
        }).start();

    }


    public static DataStack getDataStack() {
        return dataStack;
    }

    public static Substitutions getSubstitutions() {
        return substitutions;
    }
}