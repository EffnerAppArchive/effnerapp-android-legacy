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
