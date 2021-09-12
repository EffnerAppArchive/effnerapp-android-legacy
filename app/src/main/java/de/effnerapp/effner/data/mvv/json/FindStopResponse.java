/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.data.mvv.json;

public class FindStopResponse {
    private boolean success;
    private String message;
    private StopItem[] results;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public StopItem[] getResults() {
        return results;
    }
}
