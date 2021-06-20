package de.effnerapp.effner.data.api.json.data;

public class Timetable {
    private int id;
    private String sClass, value, created_by, created_at;

    public int getId() {
        return id;
    }

    public String getSClass() {
        return sClass;
    }

    public String getValue() {
        return value;
    }

    public String getCreatedBy() {
        return created_by;
    }

    public String getCreatedAt() {
        return created_at;
    }
}
