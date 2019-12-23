package de.effnerapp.effner.ui.settings;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import java.util.Date;
import java.util.Objects;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_PREF_NOTIFICATION_SWITCH = "notifications";
    private static final String KEY_PREF_NOTIFICATION_TIME = "time";
    private static final String KEY_PREF_NIGHT_MODE = "nightmode";
    private static final String KEY_PREF_LOGOUT = "logout";
    //public static final String KEY_PREF_SECURITY_STATS = "anonymStats";
    private Preference timePickerDialog;
    private PreferenceCategory notificationCategory;
    private Intent splashIntent;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MainActivity.pageTextView.setText(R.string.title_settings);
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        splashIntent = new Intent(getContext(), SplashActivity.class);
        notificationCategory = new PreferenceCategory(context);
        notificationCategory.setKey("notifications_category");
        notificationCategory.setTitle("Notifications");
        screen.addPreference(notificationCategory);

        SwitchPreference notificationPreference = new SwitchPreference(context);
        notificationPreference.setKey("notifications");
        notificationPreference.setTitle("Erhalte Nachrichten über neue Vertretungen");
        notificationPreference.setDefaultValue(false);
        notificationPreference.setIcon(R.drawable.ic_notifications_active_black_24dp);
        notificationPreference.setEnabled(false);
        notificationCategory.addPreference(notificationPreference);


        timePickerDialog = new Preference(context);
        timePickerDialog.setKey("time");
        timePickerDialog.setTitle("Uhrzeit");
        timePickerDialog.setSummary("Stelle ein wann die Benachrichtigung kommen soll");
        timePickerDialog.setIcon(R.drawable.ic_access_alarm_black_24dp);
        if (notificationPreference.isChecked()) {
            notificationCategory.addPreference(timePickerDialog);
        }

        timePickerDialog.setOnPreferenceClickListener(preference -> {
            Date date = new Date();

            int minute = date.getMinutes();
            int hour = date.getHours();

            String time = SplashActivity.sharedPreferences.getString(KEY_PREF_NOTIFICATION_TIME, "");
            if (!time.equals("")) {
                String[] timeSplit = time.split(":");
                hour = Integer.parseInt(timeSplit[0]);
                minute = Integer.parseInt(timeSplit[1]);
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (timePicker, hour1, minute1) -> {

                System.out.println("TimePicker: " + hour1 + ":" + minute1);
                String min;
                if (minute1 < 10) {
                    min = "0" + minute1;
                } else {
                    min = minute1 + "";
                }

                String hou;
                if (hour1 < 10) {
                    hou = "0" + hour1;
                } else {
                    hou = hour1 + "";
                }

                SplashActivity.sharedPreferences.edit().putString(KEY_PREF_NOTIFICATION_TIME, hou + ":" + min).apply();

            }, hour, minute, true);
            timePickerDialog.show();
            return true;
        });


//        PreferenceCategory personalCategory = new PreferenceCategory(context);
//        personalCategory.setKey("personal");
//        personalCategory.setTitle("Persönliche Daten");
//        screen.addPreference(personalCategory);

//        ListPreference listPreference = new ListPreference(context);
//        listPreference.setKey("class");
//        listPreference.setTitle("Deine Klasse");
//        listPreference.setSummary("Wähle deine Klasse aus");
//        listPreference.setDefaultValue("5A");
//        listPreference.setIcon(R.drawable.ic_group_black_24dp);
//        listPreference.setEntries(Strings.Klassen);  //Why is it
//        listPreference.setEntryValues(Strings.Klassen); //the same???
//        personalCategory.addPreference(listPreference);

        PreferenceCategory miscCategory = new PreferenceCategory(context);
        miscCategory.setKey("misc");
        miscCategory.setTitle("Verschiedenes");
        screen.addPreference(miscCategory);

        SwitchPreference nightModePreference = new SwitchPreference(context);
        nightModePreference.setKey("nightmode");
        nightModePreference.setTitle("Night-Mode :D");
        nightModePreference.setSummary("Aktiviere den Nachtmodus!");
        nightModePreference.setDefaultValue(false);
        nightModePreference.setIcon(R.drawable.ic_lightbulb_outline_black_24dp);
        nightModePreference.setEnabled(false);
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
        aboutCategory.addPreference(feedbackPreference);

        //get VERSION_NAME
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;

        Preference buildPreference = new Preference(context);
        buildPreference.setKey("build");
        buildPreference.setTitle("Build Version");
        buildPreference.setSummary(version);
        buildPreference.setIcon(R.drawable.ic_info_black_24dp);
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
        accountCategory.addPreference(classPreference);

        Preference usernamePreference = new Preference(context);
        usernamePreference.setKey("class");
        usernamePreference.setTitle("Dein Benutzername");
        usernamePreference.setSummary(SplashActivity.sharedPreferences.getString("APP_USER_USERNAME", "NONE"));
        usernamePreference.setIcon(R.drawable.ic_account_circle_black_24dp);
        accountCategory.addPreference(usernamePreference);

        Preference logoutPreference = new Preference(context);
        logoutPreference.setKey("logout");
        logoutPreference.setTitle("Abmelden");
        logoutPreference.setSummary("Melde dich ab!");
        logoutPreference.setIcon(R.drawable.ic_cancel_black_24dp);
        accountCategory.addPreference(logoutPreference);

        feedbackPreference.setOnPreferenceClickListener(preference -> {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:info@effnerapp.de"));
            startActivity(browserIntent);

            return true;
        });

        buildPreference.setOnPreferenceClickListener(preference -> {
            Toast toast = Toast.makeText(context, "Build: " + version, Toast.LENGTH_SHORT);
            toast.show();

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
                    notificationCategory.addPreference(timePickerDialog);

                } else {
                    notificationCategory.removePreference(timePickerDialog);
                }
                Objects.requireNonNull(getActivity()).finish();
                startActivity(splashIntent);
                break;
            case KEY_PREF_NOTIFICATION_TIME:

                Log.i("NOTIFICATION_TIME", "Preference value was updated to: " + sharedPreferences.getString(key, ""));
                Objects.requireNonNull(getActivity()).finish();
                startActivity(splashIntent);
                break;
            case KEY_PREF_NIGHT_MODE:
                Intent intent = Objects.requireNonNull(getActivity()).getIntent();
                getActivity().finish();
                startActivity(intent);
                break;
            case KEY_PREF_LOGOUT:
                Log.i("LOGOUT_PREF", "Logging out!");
                SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                editor.clear().apply();

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
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


}
