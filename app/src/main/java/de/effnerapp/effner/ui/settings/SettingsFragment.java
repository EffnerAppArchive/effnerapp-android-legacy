package de.effnerapp.effner.ui.settings;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
    private static final String KEY_PREF_DEV_DOTIFICATIONS = "DEV_NOTIFICATIONS";
    private SwitchPreference notificationPreference;
    private String sClass;
    private int DEV_MODE_ENABLE_COUNT = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MainActivity.pageTextView.setText(R.string.title_settings);
        sClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        PreferenceCategory notificationCategory = new PreferenceCategory(context);
        notificationCategory.setKey("notifications_category");
        notificationCategory.setTitle("Benachrichtigungen");
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

        PreferenceCategory aboutCategory = new PreferenceCategory(context);
        aboutCategory.setKey("about");
        aboutCategory.setTitle("Über");
        screen.addPreference(aboutCategory);

        Preference feedbackPreference = new Preference(context);
        feedbackPreference.setKey("feedback");
        feedbackPreference.setTitle("Ein kleines Feedback? ❤");
        feedbackPreference.setSummary("Was findest du gut und was sollen wir besser machen?");
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
        buildPreference.setTitle("App-Version");
        buildPreference.setSummary("Version: " + version + ", Build: " + build);
        buildPreference.setIcon(R.drawable.ic_build_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            buildPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        aboutCategory.addPreference(buildPreference);

        Preference creditsPreference = new Preference(context);
        creditsPreference.setKey("credits");
        creditsPreference.setTitle("Über die App");
        creditsPreference.setIcon(R.drawable.ic_info_black_24dp);
        if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
            creditsPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        aboutCategory.addPreference(creditsPreference);

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
        usernamePreference.setSummary(SplashActivity.sharedPreferences.getString("APP_USER_USERNAME", "Du besitzt keinen Benutzernamen"));
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

        if (SplashActivity.sharedPreferences.getBoolean("APP_DEV_MODE_ENABLE", false)) {
            PreferenceCategory developerCategory = new PreferenceCategory(context);
            developerCategory.setKey("developer");
            developerCategory.setTitle("Entwickleroptionen");
            screen.addPreference(developerCategory);

            Preference tokenPreference = new Preference(context);
            tokenPreference.setKey("token");
            tokenPreference.setIcon(R.drawable.ic_developer_mode_black_24dp);
            if(SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
                tokenPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
            }
            tokenPreference.setTitle("App-Token");
            tokenPreference.setSummary(SplashActivity.sharedPreferences.getString("APP_AUTH_TOKEN", ""));
            developerCategory.addPreference(tokenPreference);

            SwitchPreference devNotifications = new SwitchPreference(context);
            devNotifications.setKey(KEY_PREF_DEV_DOTIFICATIONS);
            devNotifications.setTitle("Developer Notification Channel");
            devNotifications.setIcon(R.drawable.ic_perm_device_information_black_24dp);
            if (SplashActivity.sharedPreferences.getBoolean(KEY_PREF_NIGHT_MODE, false)) {
                tokenPreference.getIcon().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
            }
            developerCategory.addPreference(devNotifications);

            tokenPreference.setOnPreferenceClickListener(preference -> {
                Toast.makeText(context, "Der Token wurde kopiert!", Toast.LENGTH_LONG).show();
                ClipboardManager clipboardManager = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Effner_APP_TOKEN", SplashActivity.sharedPreferences.getString("APP_AUTH_TOKEN", ""));
                assert clipboardManager != null;
                clipboardManager.setPrimaryClip(clipData);
                return true;
            });
        }
        feedbackPreference.setOnPreferenceClickListener(preference -> {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Feedback")
                    .setMessage("Melde dich bei uns in der Klasse 10B oder schreib uns eine E-Mail, wenn du uns Verbesserungsvorschläge mitteilen möchtest oder Probleme mit der App hast!\nWenn du willst, kannst du die App im Play-Store bewerten! 😊👌")
                    .setPositiveButton("E-Mail senden", (dialogInterface, i) -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:info@effnerapp.de"));
                        startActivity(browserIntent);
                    })
                    .setNegativeButton("App bewerten", (dialogInterface, i) -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=de.effnerapp.effner"));
                        startActivity(browserIntent);
                    })
                    .setNeutralButton("Ok", null);

            dialog.show();


            return true;
        });

        buildPreference.setOnPreferenceClickListener(preference -> {
            if(SplashActivity.sharedPreferences.getBoolean("APP_DEV_MODE_ENABLE", false)) {
                Toast.makeText(context, "Der Entwicklermodus ist schon aktiviert!", Toast.LENGTH_LONG).show();
            } else {
                DEV_MODE_ENABLE_COUNT++;
                if(DEV_MODE_ENABLE_COUNT > 5 && DEV_MODE_ENABLE_COUNT < 10) {
                    Toast.makeText(context, "Sie sind in " + (10 - DEV_MODE_ENABLE_COUNT) + " Schritten ein Entwickler!", Toast.LENGTH_LONG).show();
                } else if(DEV_MODE_ENABLE_COUNT >= 10) {
                    Toast.makeText(context, "Der Entwicklermodus ist jetzt aktiviert!", Toast.LENGTH_LONG).show();
                    SplashActivity.sharedPreferences.edit().putBoolean("APP_DEV_MODE_ENABLE", true).apply();
                }
            }
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

        creditsPreference.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Über die App")
                    .setMessage("EffnerApp - by Luis & Sebi!\n\n\n\n© 2020 EffnerApp - Danke an alle Mitwirkenden ❤")
                    .setPositiveButton("Schließen", null);
            dialog.show();
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
            case KEY_PREF_DEV_DOTIFICATIONS:
                Log.i("DEV_NOTIFICATION_SWITCH", "Preference value was updated to: " + sharedPreferences.getBoolean(key, false));
                if (sharedPreferences.getBoolean(key, false)) {
                    FirebaseMessaging.getInstance().subscribeToTopic("APP_DEVELOPER_NOTIFICATIONS").addOnCompleteListener(task -> {
                        boolean success = task.isSuccessful();
                        if (!success) {
                            notificationPreference.setEnabled(false);
                        }
                    });
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("APP_DEVELOPER_NOTIFICATIONS").addOnCompleteListener(task -> {
                        boolean success = task.isSuccessful();
                        if (!success) {
                            notificationPreference.setEnabled(true);
                        }
                    });
                }
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