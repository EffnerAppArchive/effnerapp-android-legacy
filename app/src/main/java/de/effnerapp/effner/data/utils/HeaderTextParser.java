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

    public String parse(Holidays[] holidays, String username) throws ParseException {
        String headerText = "false";
        boolean currentlyHolidays = false;
        for (Holidays holiday : holidays) {
            String name = holiday.getName().substring(0, 1).toUpperCase() + holiday.getName().substring(1).toLowerCase();
            String start = holiday.getStart();
            String end = holiday.getEnd();

            Date currentDate = new Date();
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);

            if (checkHolidays(currentDate, startDate, endDate)) {
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

        if(username != null && !username.isEmpty()) {
            headerText = "Hallo " + username + "!\n" + headerText;
        }
        return headerText;
    }

    private boolean checkHolidays(Date date, Date start, Date end) {
        return date.after(start) && date.before(end);
    }

}
