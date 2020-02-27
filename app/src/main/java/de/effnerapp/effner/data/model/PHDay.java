package de.effnerapp.effner.data.model;

public class PHDay {
    private String isFeiertag, datum, title;

    public boolean isPhDay() {
        return isFeiertag.equals("1");
    }

    public String getDate() {
        return datum;
    }

    public String getTitle() {
        return title;
    }
}
