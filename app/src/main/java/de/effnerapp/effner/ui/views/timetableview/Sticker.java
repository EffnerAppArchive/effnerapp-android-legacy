/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 18:21.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.views.timetableview;

import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class Sticker implements Serializable {
    private final ArrayList<TextView> view;
    private final ArrayList<Schedule> schedules;

    public Sticker() {
        this.view = new ArrayList<>();
        this.schedules = new ArrayList<>();
    }

    public void addTextView(TextView v) {
        view.add(v);
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }

    public ArrayList<TextView> getView() {
        return view;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }
}
