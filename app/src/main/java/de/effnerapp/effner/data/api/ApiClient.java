/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 22.06.21, 19:43.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.api;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.json.ApiResponse;
import de.effnerapp.effner.data.api.json.data.DataResponse;
import de.effnerapp.effner.tools.misc.HashTools;
import de.effnerapp.effner.tools.misc.Promise;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {
    private static ApiClient instance;
    private final Gson gson = new Gson();
    private final String url, id, password;

    private DataResponse data;

    public ApiClient(Context context) {
        instance = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.url = context.getString(R.string.uri_api_get_data) + "?class=" + sharedPreferences.getString("APP_USER_CLASS", "");
        this.id = sharedPreferences.getString("APP_DSB_LOGIN_ID", "");
        this.password = sharedPreferences.getString("APP_DSB_LOGIN_PASSWORD", "");
    }

    public void loadData(Promise<DataResponse, String> promise) {

        OkHttpClient client = new OkHttpClient();

        long now = System.currentTimeMillis();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Basic " + HashTools.sha512(id + ":" + password + ":" + now))
                .header("X-Time", String.valueOf(now))
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
                        data = apiResponse.getData();
                        promise.accept(data);
                    } else {
                        promise.reject(apiResponse.getStatus().getError());
                    }
                } catch (JsonSyntaxException e) {
                    promise.reject(response.code() + " " + response.message());
                }
            }
        });
    }

    public DataResponse getData() {
        return data;
    }

    public static ApiClient getInstance() {
        return instance;
    }
}
