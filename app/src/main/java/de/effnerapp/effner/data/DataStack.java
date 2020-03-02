package de.effnerapp.effner.data;

import de.effnerapp.effner.data.model.AppColor;
import de.effnerapp.effner.data.model.Content;
import de.effnerapp.effner.data.model.Holidays;
import de.effnerapp.effner.data.model.News;
import de.effnerapp.effner.data.model.PHDay;
import de.effnerapp.effner.data.model.Schooltest;
import de.effnerapp.effner.data.model.TDay;
import de.effnerapp.effner.data.model.Term;

public class DataStack {

    private Schooltest[] schooltests;
    private Term[] terms;
    private Holidays[] holidays;
    private PHDay phday;
    private TDay[] timetable;
    private Content[] content;
    private AppColor[] colors;
    private String username;
    private News[] news;


    public DataStack() {
        // requires empty constructor
    }

    public Schooltest[] getSchooltests() {
        return schooltests;
    }

    public Term[] getTerms() {
        return terms;
    }

    public Holidays[] getHolidays() {
        return holidays;
    }

    public PHDay getPhday() {
        return phday;
    }

    public TDay[] getTimetable() {
        return timetable;
    }

    public Content[] getContent() {
        return content;
    }

    public Content getContentByKey(String key) {
        for (Content c : content) {
            if (c.getName().equals(key)) {
                return c;
            }
        }
        return null;
    }

    public AppColor[] getColors() {
        return colors;
    }

    public AppColor getColorByKey(String key) {
        for (AppColor color : colors) {
            if (color.getName().equals(key)) {
                return color;
            }
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public News[] getNews() {
        return news;
    }
}
