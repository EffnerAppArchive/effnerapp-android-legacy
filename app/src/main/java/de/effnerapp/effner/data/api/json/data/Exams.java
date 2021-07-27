/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 27.07.21, 15:08.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api.json.data;

import com.google.gson.annotations.SerializedName;

public class Exams {
    @SerializedName("class")
    private String sClass;

    private String createdAt;

    private Exam[] exams;

    public String getsClass() {
        return sClass;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Exam[] getExams() {
        return exams;
    }
}
