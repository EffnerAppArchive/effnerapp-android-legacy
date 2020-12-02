package de.effnerapp.effner.data.mvv;

public class MvvResponse {
    private String error;
    private Departure[] departures;
    private Notification[] notifications;

    public String getError() {
        return error;
    }

    public Departure[] getDepartures() {
        return departures;
    }

    public Notification[] getNotifications() {
        return notifications;
    }
}
