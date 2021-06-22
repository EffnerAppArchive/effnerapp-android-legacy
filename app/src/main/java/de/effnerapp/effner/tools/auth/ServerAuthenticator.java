/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 20:06.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.tools.auth;

import android.app.Activity;

import de.effnerapp.effner.tools.error.ErrorUtils;

public class ServerAuthenticator {
    private final Activity activity;

    public ServerAuthenticator(Activity activity) {
        this.activity = activity;
    }

    public boolean register(String id, String password, String sClass) {
        ErrorUtils errorUtils = ErrorUtils.with(activity);

        RegistrationManager registrationManager = new RegistrationManager(activity);

        boolean result = registrationManager.register(id, password, sClass);

        if (!registrationManager.isError()) {
            return result;
        } else {
            System.out.println(registrationManager.getError());
            if (registrationManager.getError().equals("AUTHENTICATION_FAILED")) {
                return false;
            }
            errorUtils.showError(registrationManager.getError(), false, true);
            return false;
        }
    }

}