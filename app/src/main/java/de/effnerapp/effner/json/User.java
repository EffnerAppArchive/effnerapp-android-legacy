package de.effnerapp.effner.json;

public class User {
    private String token, sClass, username;

    public User(String token, String sClass, String username) {
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
