package de.effnerapp.effner.data;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import de.effnerapp.effner.data.model.AppColor;
import de.effnerapp.effner.data.model.Content;
import de.effnerapp.effner.data.model.Holidays;
import de.effnerapp.effner.data.model.Schooltest;
import de.effnerapp.effner.data.model.TDay;
import de.effnerapp.effner.data.model.Term;
import lombok.Getter;
import lombok.Setter;

public class DataStack {

    private Schooltest[] schooltests;
    private Term[] terms;
    private Holidays[] holidays;
    private TDay[] timetable;
    private Content[] content;
    private AppColor[] colors;



    public DataStack() {
        // requires empty constructor
    }

    public Schooltest[] getSchooltests() {
        return schooltests;
    }

    public void setSchooltests(Schooltest[] schooltests) {
        this.schooltests = schooltests;
    }

    public Term[] getTerms() {
        return terms;
    }

    public void setTerms(Term[] terms) {
        this.terms = terms;
    }

    public Holidays[] getHolidays() {
        return holidays;
    }

    public void setHolidays(Holidays[] holidays) {
        this.holidays = holidays;
    }

    public TDay[] getTimetable() {
        return timetable;
    }

    public void setTimetable(TDay[] timetable) {
        this.timetable = timetable;
    }

    public Content[] getContent() {
        return content;
    }

    public void setContent(Content[] content) {
        this.content = content;
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

    public void setColors(AppColor[] colors) {
        this.colors = colors;
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
