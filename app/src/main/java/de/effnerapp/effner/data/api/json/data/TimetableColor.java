/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:17.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api.json.data;

import android.graphics.Color;

public class TimetableColor {
    public static final TimetableColor BLACK = new TimetableColor("#000");
    private final String color;
    private String[] subjects;

    public TimetableColor(String color) {
        this.color = color;
    }

    public String[] getSubjects() {
        return subjects;
    }

    public int getColorValue() {
        return Color.parseColor(color);
    }
}
