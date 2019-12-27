package de.effnerapp.effner.data.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import de.effnerapp.effner.data.model.Holidays;

public class HeaderTextParser {
    private GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.GERMAN);
    public HeaderTextParser() {

    }

    public String parse(Holidays[] holidays) throws ParseException {
        String headerText = "false";
        int days2weekend = 42;
        for (Holidays holiday : holidays) {
            String name = holiday.getName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            String start = holiday.getStart();
            String end = holiday.getEnd();

            Date currentDate = new Date();
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);

            if (checkHolidays(currentDate, startDate, endDate)) {
                headerText = "Es sind " + name;
            }
        }

        if (headerText.equals("false")) {
            boolean weekend = false;


            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    days2weekend = 5;
                    break;
                case Calendar.TUESDAY:
                    days2weekend = 4;
                    break;
                case Calendar.WEDNESDAY:
                    days2weekend = 3;
                    break;
                case Calendar.THURSDAY:
                    days2weekend = 2;
                    break;
                case Calendar.FRIDAY:
                    days2weekend = 1;
                    break;
                case Calendar.SATURDAY:
                case Calendar.SUNDAY:
                    days2weekend = 0;
                    weekend = true;
                    break;
            }

            if (weekend) {
                headerText = "Es ist Wochenende!";
            } else {
                headerText = "Noch " + days2weekend + " Tage bis zum Wochenende!";
            }
        }
        return headerText;
    }

    private boolean checkHolidays(Date date, Date start, Date end) {
        return date.after(start) && date.before(end);
    }

}
