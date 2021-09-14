/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 14.09.21, 20:19.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.tools.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.json.ApiResponse;
import de.effnerapp.effner.tools.misc.HashTools;
import de.effnerapp.effner.tools.misc.Promise;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerAuthenticator {
    private static final Gson gson = new Gson();
    private final SharedPreferences sharedPreferences;
    private final String url;

    public ServerAuthenticator(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.url = context.getString(R.string.uri_api_login);
    }

    public void login(String id, String password, String sClass, Promise<Void, String> promise) {
        // reset firebase instance
        FirebaseInstallations.getInstance().delete();

        OkHttpClient client = new OkHttpClient();

        long now = System.currentTimeMillis();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Basic " + HashTools.sha512(id + ":" + password + ":" + now))
                .header("X-Time", String.valueOf(now))
                .post(RequestBody.create(new byte[0]))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                promise.reject(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();
                try {
                    ApiResponse apiResponse = gson.fromJson(res, ApiResponse.class);
                    if (apiResponse.getStatus().isLogin()) {
                        addAccount(id, password, sClass);
                        promise.accept(null);
                    } else if (apiResponse.getStatus().getError() != null) {
                        promise.reject(apiResponse.getStatus().getError());
                    } else {
                        promise.reject("ERR " + response.code() + " " + response.message());
                    }
                } catch (IllegalStateException | JsonSyntaxException e) {
                    promise.reject("ERR " + response.code() + " " + response.message());
                }
            }
        });
    }

    private void addAccount(String id, String password, String sClass) {
        sharedPreferences.edit()
                .putBoolean("APP_REGISTERED", true)
                .putString("APP_DSB_LOGIN_ID", id)
                .putString("APP_DSB_LOGIN_PASSWORD", password)
                .putString("APP_USER_CLASS", sClass)
                .apply();

        // subscribe to push notifications
        FirebaseMessaging.getInstance().subscribeToTopic("APP_GENERAL_NOTIFICATIONS");
        FirebaseMessaging.getInstance().subscribeToTopic("APP_SUBSTITUTION_NOTIFICATIONS_" + sClass);
        sharedPreferences.edit().putBoolean("APP_NOTIFICATIONS", true).apply();
    }
}
