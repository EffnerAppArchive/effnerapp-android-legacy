package de.effnerapp.effner.tools.auth;

import android.app.Activity;
import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

import de.effnerapp.effner.tools.error.ErrorUtils;

public class ServerAuthenticator {
    private Context context;
    private Activity activity;

    public ServerAuthenticator(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public boolean register(String id, String password, String sClass, String username) {
        ErrorUtils errorUtils = new ErrorUtils(context, activity);

        Timer timer = new Timer();
        RegistrationManager registrationManager = new RegistrationManager(context);

        timer.schedule(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if (i >= 10) {
                    errorUtils.showTimeout(false);
                    timer.cancel();
                    return;
                }
                i++;
            }
        }, 0, 1000);

        boolean result = registrationManager.register(id, password, sClass, username);
        timer.cancel();

        if(!registrationManager.getError().isError()) {
            return result;
        } else {
            errorUtils.showError(registrationManager.getError().getMsg(), false);
            return false;
        }
    }

}