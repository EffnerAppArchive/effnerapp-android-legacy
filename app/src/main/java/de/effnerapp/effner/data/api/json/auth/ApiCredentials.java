package de.effnerapp.effner.data.api.json.auth;

public class ApiCredentials {
    private final String token;
    private final String sClass;
    private final String username;

    public ApiCredentials(String token, String sClass, String username) {
        this.token = token;
        this.sClass = sClass;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getsClass() {
        return sClass;
    }

    public String getUsername() {
        return username;
    }
}
