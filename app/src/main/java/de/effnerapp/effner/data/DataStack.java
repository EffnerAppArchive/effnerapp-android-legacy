package de.effnerapp.effner.data;

import de.effnerapp.effner.data.model.Holidays;
import de.effnerapp.effner.data.model.Schooltest;
import de.effnerapp.effner.data.model.TDay;
import de.effnerapp.effner.data.model.Term;

public class DataStack {
    private Schooltest[] schooltests;
    private Term[] terms;
    private Holidays[] holidays;
    private TDay[] timetable;

    public DataStack() {

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

    public void setHolydays(Holidays[] holydays) {
        this.holidays = holydays;
    }

    public TDay[] getTimetable() {
        return timetable;
    }

    public void setTimetable(TDay[] timetable) {
        this.timetable = timetable;
    }
}
