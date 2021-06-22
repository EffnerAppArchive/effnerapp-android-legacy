/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:20.
 * Copyright (c) 2021 EffnerApp.
 */

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
