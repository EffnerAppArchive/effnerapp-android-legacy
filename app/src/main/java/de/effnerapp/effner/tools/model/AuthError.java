package de.effnerapp.effner.tools.model;

public class AuthError {
    private boolean isError;
    private String msg;

    public AuthError(boolean isError, String msg) {
        this.isError = isError;
        this.msg = msg;
    }

    public boolean isError() {
        return isError;
    }

    public String getMsg() {
        return msg;
    }
}
