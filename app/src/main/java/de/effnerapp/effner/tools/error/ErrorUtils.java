package de.effnerapp.effner.tools.error;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

public class ErrorUtils {
    private final Activity activity;

    public ErrorUtils(Activity activity) {
        this.activity = activity;
    }

    public void showError(String errorMessage, boolean recreateOnRetry, boolean showNegativeButton) {
        Log.e("ServerAuth", "Error!");
        AlertDialog.Builder alert = buildAlert("Anmeldevorgang fehlgeschlagen!", errorMessage, recreateOnRetry, showNegativeButton);
        activity.runOnUiThread(alert::show);
    }

    @SuppressWarnings("SameParameterValue")
    private AlertDialog.Builder buildAlert(String title, String message, boolean recreateOnRetry, boolean showNegativeButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Schießen", (dialogInterface, i) -> activity.finish());

        if (showNegativeButton) {
            builder.setNegativeButton("Neu Versuchen", recreateOnRetry ? (dialogInterface, i) -> activity.recreate() : null);
        }

        return builder;
    }
}
