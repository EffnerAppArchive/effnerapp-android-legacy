package de.effnerapp.effner.data;

import de.effnerapp.effner.data.model.AppColor;
import de.effnerapp.effner.data.model.Content;
import de.effnerapp.effner.data.model.DayInformation;
import de.effnerapp.effner.data.model.Holidays;
import de.effnerapp.effner.data.model.News;
import de.effnerapp.effner.data.model.Schooltest;
import de.effnerapp.effner.data.model.Term;
import de.effnerapp.effner.data.model.Timetable;
import de.effnerapp.effner.json.LoginStatus;

public class DataStack {

    private LoginStatus status;
    private String username;
    private Term[] terms;
    private Schooltest[] schooltests;
    private Holidays[] holidays;
    private News[] news;
    private DayInformation dayInformation;
    private Timetable timetable;
    private Content[] content;
    private AppColor[] colors;

    public DataStack() {
        // requires empty constructor
    }

    public LoginStatus getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public Term[] getTerms() {
        return terms;
    }

    public Schooltest[] getSchooltests() {
        return schooltests;
    }

    public Holidays[] getHolidays() {
        return holidays;
    }

    public News[] getNews() {
        return news;
    }

    public DayInformation getDayInformation() {
        return dayInformation;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public Content[] getContent() {
        return content;
    }

    public AppColor[] getColors() {
        return colors;
    }

    public Content getContentByKey(String key) {
        for (Content c : content) {
            if (c.getName().equals(key)) {
                return c;
            }
        }
        return null;
    }

    public AppColor getColorByKey(String key) {
        for (AppColor color : colors) {
            if (color.getName().equals(key)) {
                return color;
            }
        }
        return null;
    }
}
