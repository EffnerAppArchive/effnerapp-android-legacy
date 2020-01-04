package de.effnerapp.effner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;
import java.util.Objects;

import de.effnerapp.effner.data.model.TDay;
import de.effnerapp.effner.data.utils.DigitsParser;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;
import static android.app.AlertDialog.THEME_HOLO_DARK;


public class TimetableActivity extends AppCompatActivity {
    private TimetableView timetable;
    private DigitsParser digitsParser = new DigitsParser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        timetable = findViewById(R.id.timetable);
        if(SplashActivity.getDataStack().getTimetable().length != 0 && !isEmpty(SplashActivity.getDataStack().getTimetable())) {
            Log.d("TA", "Generating timetable!");

            ArrayList<Schedule> schedules = new ArrayList<>();


            for(TDay day : SplashActivity.getDataStack().getTimetable()) {
                int dayI = day.getId() -1;
                for (int i = 0; i < 10 ; i++) {
                    String text = null;
                    try {
                        text = Objects.requireNonNull(day.getClass().getField(digitsParser.parse(i)).get(day)).toString();
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    assert text != null;
                    if(!text.equals("-")) {
                        Schedule schedule = new Schedule();
                        schedule.setClassTitle(text);
                        schedule.setDay(dayI);
                        schedule.setStartTime(new Time(i + 1, 0));
                        schedule.setEndTime(new Time(i + 2, 0));
                        schedules.add(schedule);
                    }
                }
            }

            timetable.add(schedules);
        } else {
            Log.d("TA", "Timetable empty!");
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Kein Stundenplan!")
                    .setMessage("Für deine Klasse wurde noch kein Stundenplan eingereicht!\nMöchtest du einen Stundenplan hochlanden?")
                    .setCancelable(false)
                    .setPositiveButton("Stundenplan hochladen", (dialogInterface, i) -> {
                       startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://portal.effnerapp.de")));
                       finish();
                    })
                    .setNegativeButton("Ok", (dialogInterface, i) -> finish());

            builder.show();

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

        switch (id) {
            case R.id.navigation_timetable_settings:
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.timetable_settings_dialog, null);
                int theme = THEME_HOLO_DARK;
                ColorPickerDialog.Builder dialog = new ColorPickerDialog.Builder(this, THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Wähle eine Farbe aus!")
                        .setPreferenceName("APP_TIMETABLE_COLOR")
                        .setPositiveButton("OK", (ColorEnvelopeListener) (envelope, fromUser) -> {

                        });
                dialog.show();
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Stundenplan-Einstellungen")
//                        .setView(view)
//                        .setPositiveButton("OK", (dialogInterface, i) -> {})
//                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty(TDay[] timetable) {
        for (TDay day : timetable) {
            for (int i = 0; i < 10; i++) {
                String text = null;
                try {
                    text = Objects.requireNonNull(day.getClass().getField(digitsParser.parse(i)).get(day)).toString();
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                assert text != null;
                if(!text.isEmpty() && !text.equals("-")) {
                    return false;
                }
            }
        }
        return true;
    }
}
