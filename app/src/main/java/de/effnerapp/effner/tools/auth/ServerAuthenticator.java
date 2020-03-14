package de.effnerapp.effner.tools.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import de.effnerapp.effner.tools.model.AuthError;
import de.effnerapp.effner.tools.model.LoginResult;

public class ServerAuthenticator {
    private Context context;
    private Activity activity;
    private static ServerAuthenticator instance;

    public ServerAuthenticator(Context context, Activity activity) {
        instance = this;
        this.context = context;
        this.activity = activity;
    }

    public LoginResult login(String token) {
        LoginResult result = new LoginResult(false, true);
        Timer timer = new Timer();
        LoginManager loginManager = new LoginManager(context);

        timer.schedule(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if (i >= 10) {
                    showTimeout(true);
                    timer.cancel();
                    return;
                }
                i++;
            }
        }, 0, 1000);

        result.setLogin(loginManager.login(token));

        timer.cancel();

        if(!loginManager.getError().isError()) {
            return result;
        } else {
            if(loginManager.getError().getMsg().equals("AUTHENTICATION_FAILED")) {
                result = new LoginResult(false, true);
            } else {
                result = new LoginResult(false, false);
                showError(loginManager.getError(), true);
            }
            return result;
        }
    }


    public boolean register(String id, String password, String sClass, String username) {
        Timer timer = new Timer();
        RegistrationManager registrationManager = new RegistrationManager(context);

        timer.schedule(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if (i >= 10) {
                    showTimeout(false);
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
            showError(registrationManager.getError(), false);
            return false;
        }
    }


    private void showError(AuthError error, boolean recreateOnRetry) {
        Log.e("ServerAuth", "Error!");
        AlertDialog.Builder alert = buildAlert("Anmeldevorgang fehlgeschlagen!", error.getMsg(), recreateOnRetry);
        activity.runOnUiThread(alert::show);
    }

    private void showTimeout(boolean recreateOnRetry) {
        Log.e("ServerAuth", "Connection timed out!");
        AlertDialog.Builder alert = buildAlert("Verbindung fehlgeschlagen!", "Ich konnte mich nicht mit meinem Server verbinden!\n" +
                "Bitte überprüfe deine Internet-Verbindung und versuche es erneut!", recreateOnRetry);
        activity.runOnUiThread(alert::show);
    }

    private AlertDialog.Builder buildAlert(String title, String message, boolean recreateOnRetry) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Neu Versuchen", recreateOnRetry ? (dialogInterface, i) -> activity.recreate() : null)
                .setPositiveButton("Schießen", (dialogInterface, i) -> activity.finish());
    }

    public static ServerAuthenticator getInstance() {
        return instance;
    }
}