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

    private String createdAt, updatedAt;

    public String getSClass() {
        return sClass;
    }

    public String[][] getLessons() {
        return lessons;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
