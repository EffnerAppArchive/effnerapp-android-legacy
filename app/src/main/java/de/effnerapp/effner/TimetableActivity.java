package de.effnerapp.effner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;
import java.util.Objects;

import de.effnerapp.effner.data.model.TDay;
import de.effnerapp.effner.ui.models.timetableview.Schedule;
import de.effnerapp.effner.ui.models.timetableview.Time;
import de.effnerapp.effner.ui.models.timetableview.TimetableView;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;


public class TimetableActivity extends AppCompatActivity {
    private TimetableView timetable;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());
        if (SplashActivity.sharedPreferences.getBoolean("APP_DESIGN_DARK", false)) {
            Log.d("TA", "Nightmode: ON");
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            Log.d("TA", "Nightmode: OFF");
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        timetable = findViewById(R.id.timetable);
        if (SplashActivity.getDataStack().getTimetable() != null && SplashActivity.getDataStack().getTimetable().length != 0 && !isEmpty(SplashActivity.getDataStack().getTimetable())) {
            Log.d("TA", "Generating timetable!");

            ArrayList<Schedule> schedules = new ArrayList<>();


            for (TDay day : SplashActivity.getDataStack().getTimetable()) {
                int dayI = day.getId() - 1;
                for (int i = 1; i <= 10; i++) {
                    String text = null;
                    try {
                        text = Objects.requireNonNull(day.getClass().getField("s" + i).get(day)).toString();
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    assert text != null;
                    if (!text.equals("-")) {
                        Schedule schedule = new Schedule();
                        schedule.setSubject(text);
                        schedule.setDay(dayI);
                        schedule.setStartTime(new Time(i, 0));
                        schedule.setEndTime(new Time(i + 1, 0));
                        schedules.add(schedule);
                    }
                }
            }
            timetable.add(schedules);
            if (SplashActivity.sharedPreferences.contains("APP_TIMETABLE_COLOR")) {
                backButton.getBackground().setColorFilter(SplashActivity.sharedPreferences.getInt("APP_TIMETABLE_COLOR", -14200620), PorterDuff.Mode.SRC_ATOP);
                backButton.setTextColor(SplashActivity.sharedPreferences.getInt("APP_TIMETABLE_TEXTCOLOR", Color.WHITE));
                for (int i = 0; i < timetable.getStickerViewSize(); i++) {
                    timetable.setColor(i, SplashActivity.sharedPreferences.getInt("APP_TIMETABLE_COLOR", -14200620));
                }
                timetable.setTextColor(SplashActivity.sharedPreferences.getInt("APP_TIMETABLE_TEXTCOLOR", Color.WHITE));
            }
        } else {
            Log.d("TA", "Timetable empty!");
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Kein Stundenplan!")
                    .setMessage("Für deine Klasse wurde noch kein Stundenplan eingereicht!\nMöchtest du einen Stundenplan hochlanden?")
                    .setCancelable(false)
                    .setPositiveButton("Stundenplan hochladen", (dialogInterface, i) -> {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://portal.effnerapp.de")));
                        finish();
                    })
                    .setNegativeButton("Abbrechen", (dialogInterface, i) -> finish());
            dialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timetable_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigation_timetable_color) {
            ColorPickerDialog.Builder dialog = new ColorPickerDialog.Builder(this, THEME_DEVICE_DEFAULT_DARK)
                    .setTitle("Wähle eine Farbe aus!")
                    .setNegativeButton("Abbrechen", null)
                    .setPositiveButton("OK", (ColorEnvelopeListener) (envelope, fromUser) -> {
                        int textColor = getTextColor(envelope.getColor());
                        SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                        editor.putInt("APP_TIMETABLE_COLOR", envelope.getColor());
                        editor.putInt("APP_TIMETABLE_TEXTCOLOR", textColor);
                        editor.apply();
                        backButton.getBackground().setColorFilter(envelope.getColor(), PorterDuff.Mode.SRC_ATOP);
                        backButton.setTextColor(textColor);
                        for (int i = 0; i < timetable.getStickerViewSize(); i++) {
                            timetable.setColor(i, envelope.getColor());
                        }
                        timetable.setTextColor(textColor);
                    });
            dialog.show();
        } else if (id == R.id.navigation_timetable_settings_reset) {
            SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
            editor.remove("APP_TIMETABLE_COLOR").apply();
            timetable.updateStickerColor();
            backButton.getBackground().setColorFilter(-14200620, PorterDuff.Mode.SRC_ATOP);
            backButton.setTextColor(Color.WHITE);
            timetable.setTextColor(Color.WHITE);
            Toast.makeText(this, "Einstellungen zurückgesetzt!", Toast.LENGTH_LONG).show();
        } else if (id == R.id.navigation_timetable_upload_timetable) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Neuer Stundenplan!")
                    .setMessage("Du kannst einen neuen Stundenplan hochladen, wenn es Änderungen oder Fehler in der aktuellen Version gibt!")
                    .setCancelable(false)
                    .setPositiveButton("Stundenplan hochladen", (dialogInterface, i) -> {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://portal.effnerapp.de")));
                    })
                    .setNegativeButton("Abbrechen", null);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private int getTextColor(int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        double brightness = (0.21 * red) + (0.72 * green) + (0.07 * blue);
        System.out.println(brightness);
        if (brightness >= 0.9) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    private boolean isEmpty(TDay[] timetable) {
        for (TDay day : timetable) {
            for (int i = 1; i <= 10; i++) {
                String text = null;
                try {
                    text = Objects.requireNonNull(day.getClass().getField("s" + i).get(day)).toString();
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (text != null && !text.isEmpty() && !text.equals("-")) {
                    return false;
                }
            }
        }
        return true;
    }
}
