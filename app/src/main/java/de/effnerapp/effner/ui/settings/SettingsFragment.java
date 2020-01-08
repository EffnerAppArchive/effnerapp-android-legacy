package de.effnerapp.effner.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_PREF_NOTIFICATION_SWITCH = "notifications";
    private static final String KEY_PREF_NIGHT_MODE = "APP_DESIGN_DARK";
    private static final String KEY_PREF_LOGOUT = "logout";
    private SwitchPreference notificationPreference;
    private String sClass;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MainActivity.pageTextView.setText(R.string.title_settings);
        sClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        PreferenceCategory notificationCategory = new PreferenceCategory(context);
        notificationCategory.setKey("notifications_category");
        notificationCategory.setTitle("Notifications");
        screen.addPreference(notificationCategory);

        notificationPreference = new SwitchPreference(context);
        notificationPreference.setKey("notifications");
        notificationPreference.setTitle("Erhalte Nachrichten über neue Vertretungen");
        notificationPreference.setDefaultValue(false);
        notificationPreference.setIcon(R.drawable.ic_notifications_active_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            notificationPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        notificationCategory.addPreference(notificationPreference);

        PreferenceCategory miscCategory = new PreferenceCategory(context);
        miscCategory.setKey("misc");
        miscCategory.setTitle("Verschiedenes");
        screen.addPreference(miscCategory);

        SwitchPreference nightModePreference = new SwitchPreference(context);
        nightModePreference.setKey("APP_DESIGN_DARK");
        nightModePreference.setTitle("Night-Mode :D");
        nightModePreference.setSummary("Aktiviere den Nachtmodus!");
        nightModePreference.setDefaultValue(false);
        nightModePreference.setIcon(R.drawable.ic_lightbulb_outline_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            nightModePreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        miscCategory.addPreference(nightModePreference);

        PreferenceCategory securityCategory = new PreferenceCategory(context);
        securityCategory.setKey("security");
        securityCategory.setTitle("Sicherheit");
        screen.addPreference(securityCategory);

        SwitchPreference anonymStatsPreference = new SwitchPreference(context);
        anonymStatsPreference.setKey("anonymStats");
        anonymStatsPreference.setTitle("Sende Anonyme Nutzungsstatistiken");
        anonymStatsPreference.setSummary("Hilf anonym die App zu verbessern ❤");
        anonymStatsPreference.setDefaultValue(false);
        anonymStatsPreference.setIcon(R.drawable.ic_timeline_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            anonymStatsPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        securityCategory.addPreference(anonymStatsPreference);

        PreferenceCategory aboutCategory = new PreferenceCategory(context);
        aboutCategory.setKey("about");
        aboutCategory.setTitle("Über");
        screen.addPreference(aboutCategory);

        Preference feedbackPreference = new Preference(context);
        feedbackPreference.setKey("issues");
        feedbackPreference.setTitle("Brauchst du Hilfe?");
        feedbackPreference.setSummary("Schreibe doch eine E-Mail");
        feedbackPreference.setIcon(R.drawable.ic_mail_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            feedbackPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        aboutCategory.addPreference(feedbackPreference);

        //get VERSION_NAME
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert info != null;
        int build = info.versionCode;
        String version = info.versionName;

        Preference buildPreference = new Preference(context);
        buildPreference.setKey("build");
        buildPreference.setTitle("Build Version");
        buildPreference.setSummary(version);
        buildPreference.setIcon(R.drawable.ic_info_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            buildPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        aboutCategory.addPreference(buildPreference);

        PreferenceCategory accountCategory = new PreferenceCategory(context);
        accountCategory.setKey("account");
        accountCategory.setTitle("Account");
        screen.addPreference(accountCategory);

        Preference classPreference = new Preference(context);
        classPreference.setKey("class");
        classPreference.setTitle("Deine Klasse");
        classPreference.setSummary(SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "NONE"));
        classPreference.setIcon(R.drawable.ic_group_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            classPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        accountCategory.addPreference(classPreference);

        Preference usernamePreference = new Preference(context);
        usernamePreference.setKey("username");
        usernamePreference.setTitle("Dein Benutzername");
        usernamePreference.setSummary(SplashActivity.sharedPreferences.getString("APP_USER_USERNAME", "NONE"));
        usernamePreference.setIcon(R.drawable.ic_account_circle_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            usernamePreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        accountCategory.addPreference(usernamePreference);

        Preference logoutPreference = new Preference(context);
        logoutPreference.setKey("logout");
        logoutPreference.setTitle("Abmelden");
        logoutPreference.setSummary("Melde dich ab!");
        logoutPreference.setIcon(R.drawable.ic_cancel_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            logoutPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        accountCategory.addPreference(logoutPreference);

        feedbackPreference.setOnPreferenceClickListener(preference -> {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:info@effnerapp.de"));
            startActivity(browserIntent);

            return true;
        });

        buildPreference.setOnPreferenceClickListener(preference -> {
            Toast.makeText(context, "Effner v" + version + ", Build: " + build, Toast.LENGTH_SHORT).show();
            return true;
        });

        classPreference.setOnPreferenceClickListener(preference -> {
            Toast.makeText(context, "Um deine Klasse zu ändern, melde dich zuerst ab!", Toast.LENGTH_LONG).show();
            return true;
        });

        logoutPreference.setOnPreferenceClickListener(preference -> {
            Log.i("LOGOUT_PREF", "Logging out!");
            SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
            editor.clear().apply();

            startActivity(new Intent(getContext(), SplashActivity.class));
            Objects.requireNonNull(getActivity()).finish();
            return true;
        });

        setPreferenceScreen(screen);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_PREF_NOTIFICATION_SWITCH:
                Log.i("NOTIFICATION_SWITCH", "Preference value was updated to: " + sharedPreferences.getBoolean(key, false));
                if (sharedPreferences.getBoolean(key, false)) {
                    FirebaseMessaging.getInstance().subscribeToTopic("APP_SUBSTITUTION_NOTIFICATIONS_" + sClass).addOnCompleteListener(task -> {
                        boolean success = task.isSuccessful();
                        if(!success) {
                            notificationPreference.setEnabled(false);
                        }
                    });
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("APP_SUBSTITUTION_NOTIFICATIONS_" + sClass ).addOnCompleteListener(task -> {
                        boolean success = task.isSuccessful();
                        if(!success) {
                            notificationPreference.setEnabled(false);
                        }
                    });
                }
                break;
            case KEY_PREF_NIGHT_MODE:
                Objects.requireNonNull(getActivity()).recreate();
                break;
            case KEY_PREF_LOGOUT:
                Log.i("LOGOUT_PREF", "Logging out!");
                //clear sharedPreferences
                SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                editor.clear().apply();
                //disable Firebase Notifications
                FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                        firebaseMessaging.unsubscribeFromTopic("APP_SUBSTITUTION_NOTIFICATIONS_" + sClass );
                        firebaseMessaging.unsubscribeFromTopic("APP_GENERAL_NOTIFICATIONS");
                startActivity(new Intent(getContext(), SplashActivity.class));
                Objects.requireNonNull(getActivity()).finish();
                break;
        }
    }

    //register and unregister SharedPreferenceListener
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        MainActivity.pageTextView.setText(R.string.title_settings);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


}
