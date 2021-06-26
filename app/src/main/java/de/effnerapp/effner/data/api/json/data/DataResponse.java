/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 22.06.21, 19:43.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api.json.data;

import java.util.Arrays;

public class DataResponse {
    private String motd;
    private Timetable timetable;
    private TimetableColor[] colors;
    private Exam[] exams;
    private Document[] documents;

    public String getMotd() {
        return motd;
    }

    public Exam[] getExams() {
        return exams;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public Document[] getDocuments() {
        return documents;
    }

    public TimetableColor[] getColors() {
        return colors;
    }

    public Document getDocumentByKey(String key) {
        for (Document document : documents) {
            if (document.getKey().equals(key)) {
                return document;
            }
        }
        return null;
    }

    public TimetableColor getTimetableColorBySubjectName(String s) {
        for (TimetableColor color : colors) {
            if (Arrays.asList(color.getSubjects()).contains(s)) {
                return color;
            }
        }
        return TimetableColor.BLACK;
    }
}