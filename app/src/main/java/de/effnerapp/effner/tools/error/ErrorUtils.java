package de.effnerapp.effner.tools.error;

import android.app.Activity;
import android.app.AlertDialog;

import de.effnerapp.effner.R;

public class ErrorUtils {
    private final Activity activity;

    private ErrorUtils(Activity activity) {
        this.activity = activity;
    }

    public static ErrorUtils with(Activity activity) {
        return new ErrorUtils(activity);
    }

    public void showError(String errorMessage, boolean recreateOnRetry, boolean showNegativeButton) {
        AlertDialog.Builder alert = buildAlert(errorMessage, recreateOnRetry, showNegativeButton);
        activity.runOnUiThread(alert::show);
    }

    private AlertDialog.Builder buildAlert(String message, boolean recreateOnRetry, boolean showNegativeButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.d_err_login)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.button_cancel, (dialogInterface, i) -> activity.finish());

        if (showNegativeButton) {
            builder.setNegativeButton(R.string.button_retry, recreateOnRetry ? (dialogInterface, i) -> activity.recreate() : null);
        }

        return builder;
    }
}
