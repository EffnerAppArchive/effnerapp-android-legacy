package de.effnerapp.effner.tools.error;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

public class ErrorUtils {
    private final Context context;
    private final Activity activity;

    public ErrorUtils(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void showError(String errorMessage, boolean recreateOnRetry) {
        Log.e("ServerAuth", "Error!");
        AlertDialog.Builder alert = buildAlert("Anmeldevorgang fehlgeschlagen!", errorMessage, recreateOnRetry);
        activity.runOnUiThread(alert::show);
    }

    public void showTimeout(boolean recreateOnRetry) {
        Log.e("ServerAuth", "Connection timed out!");
        AlertDialog.Builder alert = buildAlert("Verbindung fehlgeschlagen!", "Ich konnte mich nicht mit meinem Server verbinden!\n" +
                "Bitte überprüfe deine Internet-Verbindung und versuche es erneut!", recreateOnRetry);
        activity.runOnUiThread(alert::show);
    }

    private AlertDialog.Builder buildAlert(String title, String message, boolean recreateOnRetry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Schießen", (dialogInterface, i) -> activity.finish());

        if (recreateOnRetry) {
            builder.setNegativeButton("Neu Versuchen", (dialogInterface, i) -> activity.recreate());
        }

        return builder;
    }
}
