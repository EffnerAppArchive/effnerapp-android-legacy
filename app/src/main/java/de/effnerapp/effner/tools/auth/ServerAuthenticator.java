package de.effnerapp.effner.tools.auth;

import android.app.Activity;
import android.widget.Toast;

import de.effnerapp.effner.tools.error.ErrorUtils;

public class ServerAuthenticator {
    private final Activity activity;

    public ServerAuthenticator(Activity activity) {
        this.activity = activity;
    }

    public boolean register(String id, String password, String sClass, String username) {
        ErrorUtils errorUtils = new ErrorUtils(activity);

        RegistrationManager registrationManager = new RegistrationManager(activity);

        boolean result = registrationManager.register(id, password, sClass, username);

        if(!registrationManager.getError().isError()) {
            return result;
        } else {
            if (registrationManager.getError().getMsg().equals("AUTHENTICATION_FAILED")) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Anmeldung fehlgeschlagen", Toast.LENGTH_SHORT).show());
                return false;
            }
            errorUtils.showError(registrationManager.getError().getMsg(), false, true);
            return false;
        }
    }

}