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

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

public class IntentHelper {
    public static void openView(Context context, Uri uri) {
        if (uri.toString().endsWith(".pdf")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
            return;
        }

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder().setToolbarColor(Color.BLACK).build());
        CustomTabsIntent intent = builder.build();
        intent.intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.launchUrl(context, uri);
    }

    public static void openView(Context context, String url) {
        openView(context, Uri.parse(url));
    }
}
