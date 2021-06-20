/*
 *  Created by SpyderScript on 21.09.2020, 10:20.
 *  Project: Effner.
 *  Copyright (c) 2020.
 */

package de.effnerapp.effner.data.api.json.data;

import android.accounts.AccountManager;

import de.effnerapp.effner.data.api.json.status.LoginStatus;
import de.effnerapp.effner.services.Authenticator;
import de.effnerapp.effner.ui.activities.main.MainActivity;

public class DataResponse {
    private LoginStatus status;
    private Schooltest[] schooltests;
    private Holidays[] holidays;
    private News[] news;
    private DayInformation dayInformation;
    private Timetable timetable;
    private Content[] content;
    private AppColor[] colors;

    public LoginStatus getStatus() {
        return status;
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
                if (c.getValue().contains("{APP_TOKEN}")) {
                    AccountManager accountManager = AccountManager.get(MainActivity.getInstance());
                    String token = accountManager.getPassword(accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)[0]);
                    c.setValue(c.getValue().replace("{APP_TOKEN}", token));
                }
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
        return AppColor.BLACK;
    }
}