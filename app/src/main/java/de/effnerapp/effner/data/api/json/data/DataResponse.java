/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.data.api.json.data;

public class DataResponse {
    private String motd;
    private Timetable timetable;
    private Exams exams;
    private Document[] documents;

    public String getMotd() {
        return motd;
    }

    public Exams getExams() {
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