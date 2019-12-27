package de.effnerapp.effner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;
import java.util.Objects;

import de.effnerapp.effner.data.model.TDay;
import de.effnerapp.effner.data.utils.DigitsParser;


public class TimetableActivity extends AppCompatActivity {
    private DigitsParser digitsParser = new DigitsParser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        TimetableView timetable = findViewById(R.id.timetable);

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
