/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 17.09.21, 21:56.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.fragments.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.ApiClient;
import de.effnerapp.effner.data.api.json.data.Timetable;
import de.effnerapp.effner.ui.activities.splash.SplashActivity;
import de.effnerapp.effner.ui.views.timetableview.Schedule;
import de.effnerapp.effner.ui.views.timetableview.Time;
import de.effnerapp.effner.ui.views.timetableview.TimetableView;

public class TimetableFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private final List<String> classes = new ArrayList<>();

    public TimetableFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        Context context = requireContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (ApiClient.getInstance() == null) {
            startActivity(new Intent(requireContext(), SplashActivity.class));
            requireActivity().finish();
            return view;
        }

        TimetableView timetable = view.findViewById(R.id.timetable);
        TextView timetableInfoText = view.findViewById(R.id.timetable_info_text);

        Spinner spinner = view.findViewById(R.id.spinner);

        classes.clear();
        for (Timetable t : ApiClient.getInstance().getData().getTimetables()) {
            classes.add(t.getSClass());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classes);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        selectDefaultItem(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPreferences.edit().putInt("APP_PREFERRED_TIMETABLE", i).apply();
                String[][] lessons = ApiClient.getInstance().getData().getTimetables()[i].getLessons();

                if (lessons != null && lessons.length != 0) {
                    try {
                        SimpleDateFormat originFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.GERMAN);
                        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
                        Date lastUpdate = originFormat.parse(ApiClient.getInstance().getData().getTimetables()[i].getUpdatedAt());
                        assert lastUpdate != null;
                        String text = "Zuletzt aktualisiert: " + targetFormat.format(lastUpdate);
                        timetableInfoText.setText(text);
                    } catch (ParseException | NullPointerException e) {
                        timetableInfoText.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                    List<Schedule> schedules = new ArrayList<>();

                    int dayI = 0;
                    for (String[] day : lessons) {
                        for (int j = 0; j < 10; j++) {
                            String lesson = day[j];

                            if (lesson != null && !lesson.isEmpty()) {
                                Schedule schedule = new Schedule();
                                schedule.setSubject(lesson);
                                schedule.setDay(dayI);
                                schedule.setStartTime(new Time(j + 1, 0));
                                schedule.setEndTime(new Time(j + 2, 0));
                                schedule.setBackgroundColor(ApiClient.getInstance().getData().getTimetables()[i].getSubjectColor(lesson).getColorValue());
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
                            .setPositiveButton(R.string.d_button_timetable_submit, (dialogInterface, i1) -> {
                                navigateHome();
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.uri_link_email_timetable))));
                            })
                            .setNegativeButton(R.string.button_cancel, (dialogInterface, i1) -> navigateHome());
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

    private void selectDefaultItem(Spinner spinner) {
        int preferredTimetable = sharedPreferences.getInt("APP_PREFERRED_TIMETABLE", 0);
        if (!classes.isEmpty()) {
            spinner.setSelection(preferredTimetable);
        }
    }

    private void navigateHome() {
        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);

        // workaround to uncheck the currently selected item
        navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
        navView.getMenu().getItem(0).setCheckable(false);

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        // navigate to fragment
        navController.navigate(R.id.navigation_home);
    }
}