/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.data.mvv.json;

public class Departure {
    private Line line;
    private String direction, track, departureDate, departurePlanned, departureLive;
    private Station station;
    private boolean inTime;

    public Line getLine() {
        return line;
    }

    public String getDirection() {
        return direction;
    }

    public String getTrack() {
        return track;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getDeparturePlanned() {
        return departurePlanned;
    }

    public String getDepartureLive() {
        return departureLive;
    }

    public Station getStation() {
        return station;
    }

    public boolean isInTime() {
        return inTime;
    }
}
