package de.effnerapp.effner;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import de.effnerapp.effner.data.dsbmobile.DSBClient;
import de.effnerapp.effner.data.utils.ApiClient;
import de.effnerapp.effner.services.Authenticator;
import de.effnerapp.effner.tools.error.ErrorUtils;
import de.effnerapp.effner.ui.login.LoginActivity;
import de.effnerapp.effner.ui.substitutions.SubstitutionsFragment;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        AccountManager accountManager = AccountManager.get(this);
        if (!sharedPreferences.getBoolean("IntroActivity.COMPLETED_ON_BOARDING", false)) {
            // set APP_DESIGN_DARK preference based on system dark mode
            detectSystemTheme();

            // start intro activity
            startActivity(new Intent(this, IntroActivity.class));
            finish();
            return;
        }

        if (sharedPreferences.getBoolean("APP_REGISTERED", false)) {
            if (accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE).length > 0) {
                String token = accountManager.getPassword(accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)[0]);
                loadData(token);
            } else {
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void loadData(String token) {

        ApiClient api = new ApiClient(this, token);

        api.loadData((isSuccess, data) -> {
            if (isSuccess && data.getStatus().isLogin()) {
                // load substitutions
                DSBClient dsbClient = new DSBClient(sharedPreferences.getString("APP_DSB_LOGIN_ID", ""), sharedPreferences.getString("APP_DSB_LOGIN_PASSWORD", ""));
                new Thread(() -> dsbClient.load(() -> {
                    Log.d("Splash", "DSB data load finished!");
                    if (SubstitutionsFragment.getInstance() != null && SubstitutionsFragment.getInstance().isVisible()) {
                        Log.d("Splash", "Substitution fragment currently visible, notifying due to data load finished.");
                        runOnUiThread(() -> SubstitutionsFragment.getInstance().onDataLoadFinished());
                    }
                })).start();

                // start MainActivity and close current activity
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                if (!isSuccess) {
                    new ErrorUtils(this).showError("Could not connect to server.", true, true);
                } else if (data.getStatus().getMsg().equals("AUTHENTICATION_FAILED")) {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    new ErrorUtils(this).showError(data.getStatus().getMsg(), true, true);
                }
            }
        });
    }

    public void detectSystemTheme() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            sharedPreferences.edit().putBoolean("APP_DESIGN_DARK", true).apply();
        }
    }
}