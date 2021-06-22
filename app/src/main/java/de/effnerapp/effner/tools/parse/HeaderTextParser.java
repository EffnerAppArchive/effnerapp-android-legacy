/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 18:21.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.tools.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import de.effnerapp.effner.data.api.json.data.DayInformation;
import de.effnerapp.effner.data.api.json.data.Holidays;

public class HeaderTextParser {
    private final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

    public HeaderTextParser() {

    }

    public String parse(Holidays[] holidays, DayInformation dayInformation) {
        String headerText = null;

        boolean currentlyHolidays = false;
        if (dayInformation.isHoliday()) {
            headerText = "Es ist " + dayInformation.getTitle();
        } else {
            for (Holidays holiday : holidays) {
                String name = holiday.getName();
                Date currentDate = new Date();
                Date start;
                Date end;
                try {
                    start = format.parse(holiday.getStart());
                    end = format.parse(holiday.getEnd());
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }

                if (checkHolidays(currentDate, start, end)) {
                    currentlyHolidays = true;
                    headerText = "Es sind " + name;
                    break;
                }
            }

            if (!currentlyHolidays) {
                int days;
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                switch (day) {
                    case Calendar.SATURDAY:
                    case Calendar.SUNDAY:
                        headerText = "Es ist Wochenende!";
                        break;
                    default:
                        days = 7 - day;
                        if (calendar.get(Calendar.HOUR_OF_DAY) >= 13) {
                            days--;
                            headerText = (days > 0) ? "Noch " + days + " Tage bis zum Wochenende!" : "Das Wochenende beginnt!";
                        } else {
                            headerText = "Noch " + days + " Tage bis zum Wochenende!";
                        }
                        break;
                }
            }
        }

        return headerText;
    }

    private boolean checkHolidays(Date date, Date start, Date end) {
        return date.after(start) && date.before(end);
    }

}
