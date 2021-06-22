/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 18:21.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api.json.status;

public class Status {
    private int status;
    private String msg;
    private long processingTime;
    private String apiVersion;

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public String getApiVersion() {
        return apiVersion;
    }
}
