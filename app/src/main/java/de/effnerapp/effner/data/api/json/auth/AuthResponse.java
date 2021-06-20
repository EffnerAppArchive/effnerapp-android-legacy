package de.effnerapp.effner.data.api.json.auth;

import de.effnerapp.effner.data.api.json.status.Status;

public class AuthResponse {
    private Status status;
    private ApiCredentials auth;

    public Status getStatus() {
        return status;
    }

    public ApiCredentials getCredentials() {
        return auth;
    }
}
