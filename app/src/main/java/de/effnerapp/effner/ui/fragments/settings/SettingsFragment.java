package de.effnerapp.effner.ui.fragments.settings;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessaging;

import de.effnerapp.effner.R;
import de.effnerapp.effner.services.Authenticator;
import de.effnerapp.effner.tools.view.IntentHelper;
import de.effnerapp.effner.ui.activities.intro.IntroActivity;
import de.effnerapp.effner.ui.activities.splash.SplashActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String sClass;

    private int BUILD_VERSION_CLICK_COUNT = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Context context = requireContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        sClass = sharedPreferences.getString("APP_USER_CLASS", "");

        AccountManager accountManager = AccountManager.get(context);

        Preference introPreference = findPreference("intro");
        Preference feedbackPreference = findPreference("feedback");
        Preference buildPreference = findPreference("build");
        Preference privacyPolicyPreference = findPreference("privacyPolicy");
        Preference imprintPreference = findPreference("imprint");
        Preference aboutPreference = findPreference("about");
        Preference classPreference = findPreference("class");
        Preference logoutPreference = findPreference("logout");

        PreferenceCategory devToolsCategory = findPreference("dev_tools_cat");
        Preference devAuthToken = findPreference("dev_auth_token");
        Preference devFirebaseToken = findPreference("dev_firebase_token");

        assert devToolsCategory != null;
        devToolsCategory.setVisible(sharedPreferences.getBoolean("APP_DEV_MODE_ENABLED", false));

        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert info != null;
        int build = info.versionCode;
        String version = info.versionName;

        assert buildPreference != null;
        buildPreference.setSummary("Version: " + version + ", Build: " + build);

        buildPreference.setOnPreferenceClickListener(preference -> {
            if (BUILD_VERSION_CLICK_COUNT >= 10 && !sharedPreferences.getBoolean("APP_DEV_MODE_ENABLED", false)) {
                sharedPreferences.edit().putBoolean("APP_DEV_MODE_ENABLED", true).apply();
                devToolsCategory.setVisible(true);
                Toast.makeText(context, "Dev tools enabled", Toast.LENGTH_SHORT).show();
                return true;
            }

            BUILD_VERSION_CLICK_COUNT++;
            return true;
        });

        assert classPreference != null;
        classPreference.setSummary(sClass);

        assert feedbackPreference != null;
        feedbackPreference.setOnPreferenceClickListener(preference -> {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.d_settings_feedback_title)
                    .setMessage(R.string.d_settings_feedback_message)
                    .setPositiveButton(R.string.d_button_send_email, (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.uri_link_email_feedback)))))
                    .setNegativeButton(R.string.d_button_rate_app, (dialogInterface, i) -> IntentHelper.openView(context, getString(R.string.uri_link_google_play)))
                    .setNeutralButton(R.string.d_button_ok, null);

            dialog.show();

            return true;
        });

        classPreference.setOnPreferenceClickListener(preference -> {
            Toast.makeText(context, R.string.t_prompt_change_class, Toast.LENGTH_SHORT).show();
            return true;
        });

        assert logoutPreference != null;
        logoutPreference.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.d_settings_logout_title)
                    .setMessage(R.string.d_settings_logout_message)
                    .setPositiveButton(R.string.d_button_logout, (dialogInterface, i) -> {

                        // clear sharedPreferences
                        sharedPreferences.edit().clear().apply();

                        // remove account
                        accountManager.removeAccountExplicitly(accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)[0]);

                        // disable Firebase Notifications
                        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                        firebaseMessaging.unsubscribeFromTopic("APP_SUBSTITUTION_NOTIFICATIONS_" + sClass);
                        firebaseMessaging.unsubscribeFromTopic("APP_GENERAL_NOTIFICATIONS");
                        startActivity(new Intent(getContext(), SplashActivity.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton(R.string.button_cancel, null);
            dialog.show();
            return true;
        });

        assert aboutPreference != null;
        aboutPreference.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.d_settings_about_title)
                    .setMessage(R.string.d_settings_about_message)
                    .setPositiveButton(R.string.d_button_close, null);
            dialog.show();
            return true;
        });

        assert introPreference != null;
        introPreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(context, IntroActivity.class));
            return true;
        });

        assert privacyPolicyPreference != null;
        privacyPolicyPreference.setOnPreferenceClickListener(preference -> {
            IntentHelper.openView(context, Uri.parse(getString(R.string.uri_link_privacy_policy)));
            return true;
        });

        assert imprintPreference != null;
        imprintPreference.setOnPreferenceClickListener(preference -> {
            IntentHelper.openView(context, Uri.parse(getString(R.string.uri_link_imprint)));
            return true;
        });

        assert devAuthToken != null;
        assert devFirebaseToken != null;

        String token = accountManager.getPassword(accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)[0]);
        devAuthToken.setSummary(token);
        devFirebaseToken.setSummary(sharedPreferences.getString("APP_FIREBASE_TOKEN", "Not available"));

        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);


        devAuthToken.setOnPreferenceClickListener(preference -> {
            ClipData clip = ClipData.newPlainText("effnerapp auth token", preference.getSummary());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            return true;
        });

        devFirebaseToken.setOnPreferenceClickListener(preference -> {
            ClipData clip = ClipData.newPlainText("effnerapp firebase token", preference.getSummary());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            return true;
        });


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == null) return;
        switch (key) {
            case "APP_NOTIFICATIONS":
                setNotifications(sharedPreferences.getBoolean(key, false));
                break;
            case "APP_DESIGN_DARK":
                if (sharedPreferences.getBoolean("APP_DESIGN_DARK", false)) {
                    ((AppCompatActivity) requireActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    ((AppCompatActivity) requireActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;
        }
    }

    private void setNotifications(boolean value) {
        if (value) {
            FirebaseMessaging.getInstance().subscribeToTopic("APP_SUBSTITUTION_NOTIFICATIONS_" + sClass);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("APP_SUBSTITUTION_NOTIFICATIONS_" + sClass);
        }
    }

    // register and unregister SharedPreferenceListener
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}