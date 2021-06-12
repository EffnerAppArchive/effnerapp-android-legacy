package de.effnerapp.effner.ui.models.timetableview;

import java.io.Serializable;

public class Schedule implements Serializable {
    private String subject = "";
    private String place = "";
    private int day = 0;
    private Time startTime;
    private Time endTime;
    private int backgroundColor;

    public Schedule() {
        this.startTime = new Time();
        this.endTime = new Time();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
