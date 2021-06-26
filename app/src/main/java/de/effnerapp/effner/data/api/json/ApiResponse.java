/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 25.06.21, 14:40.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api.json;

import de.effnerapp.effner.data.api.json.data.DataResponse;
import de.effnerapp.effner.data.api.json.status.LoginStatus;

public class ApiResponse {
    private LoginStatus status;
    private DataResponse data;

    public LoginStatus getStatus() {
        return status;
    }

    public DataResponse getData() {
        return data;
    }
}
