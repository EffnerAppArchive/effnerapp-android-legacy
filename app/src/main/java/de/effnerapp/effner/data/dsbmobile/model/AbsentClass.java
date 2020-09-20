package de.effnerapp.effner.data.dsbmobile.model;

public class AbsentClass {
    private final String date;
    private final String sClass;
    private final String period;

    public AbsentClass(String date, String sClass, String period) {
        this.date = date;
        this.sClass = sClass;
        this.period = period;
    }

    public String getDate() {
        return date;
    }

    public String getSClass() {
        return sClass;
    }

    public String getPeriod() {
        return period;
    }

}
