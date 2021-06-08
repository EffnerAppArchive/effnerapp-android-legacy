package de.effnerapp.effner.json;

public class BaseResponse {
    private final Status status;

    public BaseResponse(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
