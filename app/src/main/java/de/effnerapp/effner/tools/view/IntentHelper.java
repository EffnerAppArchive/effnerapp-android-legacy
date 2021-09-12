/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 22.06.21, 19:43.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.tools.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

public class IntentHelper {
    public static void openView(Context context, Uri uri) {
        String contentType = uri.toString().endsWith(".pdf") ? "application/pdf" : null;
        openView(context, uri, contentType);
    }

    public static void openView(Context context, String url) {
        openView(context, Uri.parse(url));
    }

    public static void openView(Context context, Uri uri, String contentType) {
        if(contentType != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, contentType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // fallback to default action_view handler
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException e1) {
                    Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder().setToolbarColor(Color.BLACK).build());
        CustomTabsIntent intent = builder.build();
        intent.intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.launchUrl(context, uri);
    }
}
