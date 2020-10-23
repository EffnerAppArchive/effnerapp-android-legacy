package de.effnerapp.effner.ui.fragments.settings;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
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

        assert classPreference != null;
        classPreference.setSummary(sClass);

        assert feedbackPreference != null;
        feedbackPreference.setOnPreferenceClickListener(preference -> {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle("Feedback")
                    .setMessage(R.string.feedbackDialog)
                    .setPositiveButton("E-Mail senden", (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:app@effnerapp.de"))))
                    .setNegativeButton("App bewerten", (dialogInterface, i) -> IntentHelper.openView(context, "https://play.google.com/store/apps/details?id=de.effnerapp.effner"))
                    .setNeutralButton("Ok", null);

            dialog.show();

            return true;
        });

        classPreference.setOnPreferenceClickListener(preference -> {
            Toast.makeText(context, "Um deine Klasse zu ändern, melde dich zuerst ab!", Toast.LENGTH_SHORT).show();
            return true;
        });

        assert logoutPreference != null;
        logoutPreference.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle("Abmelden?")
                    .setMessage("Willst du dich wirklich abmelden?")
                    .setPositiveButton("Abmelden", (dialogInterface, i) -> {
                        Log.i("LOGOUT_PREF", "Logging out!");
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
                    .setNegativeButton("Abbrechen", null);
            dialog.show();
            return true;
        });

        assert aboutPreference != null;
        aboutPreference.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle("Über die App")
                    .setMessage("EffnerApp - by Luis & Sebi!\n\n\n\n© 2020 EffnerApp - Danke an alle Mitwirkenden ❤")
                    .setPositiveButton("Schließen", null);
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
            IntentHelper.openView(context, "https://go.effnerapp.de/privacy");
            return true;
        });

        assert imprintPreference != null;
        imprintPreference.setOnPreferenceClickListener(preference -> {
            IntentHelper.openView(context, "https://go.effnerapp.de/imprint");
            return true;
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == null) return;
        switch (key) {
            case "APP_NOTIFICATIONS":
                Log.i("NOTIFICATION_SWITCH", "Preference value was updated to: " + sharedPreferences.getBoolean(key, false));
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