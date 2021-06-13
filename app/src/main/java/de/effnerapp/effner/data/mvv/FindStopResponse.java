package de.effnerapp.effner.data.mvv;

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
