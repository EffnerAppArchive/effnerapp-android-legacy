/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:20.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.timetable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.ApiClient;
import de.effnerapp.effner.ui.views.timetableview.Schedule;
import de.effnerapp.effner.ui.views.timetableview.Time;
import de.effnerapp.effner.ui.views.timetableview.TimetableView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimetableFragment extends Fragment {


    public TimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        Context context = requireContext();

        TimetableView timetable = view.findViewById(R.id.timetable);
        TextView timetableInfoText = view.findViewById(R.id.timetable_info_text);

        String[][] data = new Gson().fromJson(ApiClient.getInstance().getData().getTimetable().getValue(), String[][].class);

        if (data != null && data.length != 0) {
            try {
                SimpleDateFormat originFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
                Date lastUpdate = originFormat.parse(ApiClient.getInstance().getData().getTimetable().getCreatedAt());
                assert lastUpdate != null;
                String text = "Zuletzt aktualisiert: " + targetFormat.format(lastUpdate);
                timetableInfoText.setText(text);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<Schedule> schedules = new ArrayList<>();

            int dayI = 0;
            for (String[] day : data) {
                for (int i = 0; i < 10; i++) {
                    String lesson = day[i];

                    if (lesson != null && !lesson.isEmpty()) {
                        Schedule schedule = new Schedule();
                        schedule.setSubject(lesson);
                        schedule.setDay(dayI);
                        schedule.setStartTime(new Time(i + 1, 0));
                        schedule.setEndTime(new Time(i + 2, 0));
                        schedule.setBackgroundColor(ApiClient.getInstance().getData().getColorByKey("COLOR_TIMETABLE_" + lesson).getColorValue());
                        schedules.add(schedule);
                    }
                }

                dayI++;
            }
            timetable.add(schedules);
        } else {
            timetableInfoText.setVisibility(View.GONE);

            AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.d_timetable_unavailable_title)
                    .setMessage(R.string.d_timetable_unavailable_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.d_button_timetable_submit, (dialogInterface, i) -> {
                        navigateHome();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.uri_link_email_timetable))));
                    })
                    .setNegativeButton(R.string.button_cancel, (dialogInterface, i) -> navigateHome());
            dialog.show();
        }
        return view;
    }

    private void navigateHome() {
        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);

        // workaround to uncheck the currently selected item
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        navView.getMenu().getItem(0).setCheckable(false);

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        // navigate to news fragment
        navController.navigate(R.id.navigation_home);
    }
}