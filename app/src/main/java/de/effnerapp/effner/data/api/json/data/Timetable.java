/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 18:21.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api.json.data;

import com.google.gson.annotations.SerializedName;

public class Timetable {
    @SerializedName("class")
    private String sClass;

    private String[][] lessons;
    private TimetableColor[] meta;

    private String updatedAt;

    public String getSClass() {
        return sClass;
    }

    public String[][] getLessons() {
        return lessons;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public TimetableColor[] getMeta() {
        return meta;
    }

    public TimetableColor getSubjectColor(String subject) {
        for(TimetableColor c : meta) {
            if(c.getSubject().equals(subject)) {
                return c;
            }
        }

        return TimetableColor.BLACK;
    }
}
