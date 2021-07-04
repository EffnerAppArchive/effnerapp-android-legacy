/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 22.06.21, 19:43.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api.json.data;

public class DataResponse {
    private String motd;
    private Timetable timetable;
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

    public Document getDocumentByKey(String key) {
        for (Document document : documents) {
            if (document.getKey().equals(key)) {
                return document;
            }
        }
        return null;
    }
}