/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:17.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api.json.data;

import android.graphics.Color;

public class TimetableColor {
    public static final TimetableColor BLACK = new TimetableColor("#000000");
    private String subject;
    private final String color;

    public TimetableColor(String color) {
        this.color = color;
    }

    public String getSubject() {
        return subject;
    }

    public int getColorValue() {
        try {
            return Color.parseColor(color);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Color.BLACK;
        }

    }
}
