/*
 *  Created by SpyderScript on 20.10.2020, 18:15.
 *  Project: Effner.
 *  Copyright (c) 2020.
 */

package de.effnerapp.effner.tools.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

public class IntentHelper {
    public static void openView(Context context, Uri uri) {
        if (uri.toString().endsWith(".pdf")) {
            openSystemView(context, uri);
            return;
        }

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(Color.BLACK);
        CustomTabsIntent intent = builder.build();
        intent.intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.launchUrl(context, uri);
    }

    public static void openView(Context context, String url) {
        openView(context, Uri.parse(url));
    }

    public static void openSystemView(Context context, Uri uri) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
