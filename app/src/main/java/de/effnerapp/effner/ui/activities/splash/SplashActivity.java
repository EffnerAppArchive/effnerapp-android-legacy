/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 20:09.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.activities.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.ApiClient;
import de.effnerapp.effner.data.api.json.data.DataResponse;
import de.effnerapp.effner.data.dsbmobile.DSBClient;
import de.effnerapp.effner.tools.error.ErrorUtils;
import de.effnerapp.effner.tools.misc.Promise;
import de.effnerapp.effner.ui.activities.intro.IntroActivity;
import de.effnerapp.effner.ui.activities.login.LoginActivity;
import de.effnerapp.effner.ui.activities.main.MainActivity;
import de.effnerapp.effner.ui.fragments.substitutions.SubstitutionsFragment;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fixes Handshake bug on Android 7.0
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            try {
                // Google Play will install latest OpenSSL
                ProviderInstaller.installIfNeeded(getApplicationContext());
                SSLContext sslContext;
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                sslContext.createSSLEngine();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                    | NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }

        if (getResources().getBoolean(R.bool.isLargeDevice)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sharedPreferences.getBoolean("IntroActivity.COMPLETED_ON_BOARDING", false)) {
            // set APP_DESIGN_DARK preference based on system dark mode
            detectSystemTheme();

            // start intro activity
            startActivity(new Intent(this, IntroActivity.class));
            finish();
            return;
        }

        if (sharedPreferences.getBoolean("APP_REGISTERED", false)) {
            loadData();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void loadData() {
        ApiClient api = new ApiClient(this);

        api.loadData(new Promise<DataResponse, String>() {
            @Override
            public void accept(DataResponse data) {
                // load substitutions
                DSBClient dsbClient = new DSBClient(sharedPreferences.getString("APP_DSB_LOGIN_ID", ""), sharedPreferences.getString("APP_DSB_LOGIN_PASSWORD", ""));
                new Thread(() -> dsbClient.load(() -> {
                    if (SubstitutionsFragment.getInstance() != null && SubstitutionsFragment.getInstance().isVisible()) {
                        runOnUiThread(() -> SubstitutionsFragment.getInstance().onDataLoadFinished());
                    }

                    return null;
                })).start();

                // start MainActivity and close current activity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void reject(String s) {
                if (s.equals("Unauthorized")) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    ErrorUtils.with(SplashActivity.this).showError(s, true, true);
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
