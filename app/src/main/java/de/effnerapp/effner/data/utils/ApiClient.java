/*
 *  Created by SpyderScript on 21.09.2020, 10:07.
 *  Project: Effner.
 *  Copyright (c) 2020.
 */

package de.effnerapp.effner.data.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import de.effnerapp.effner.data.DataResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String BASE_URL = "https://api.effnerapp.de:45890/rest";
    private final String token;
    private PackageInfo info;

    public ApiClient(Context context, String token) {
        this.token = token;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadData(ApiCallback callback) {
        String url = BASE_URL + "?token=" + token + "&app_version=" + info.versionName;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();

                try {
                    DataResponse dataResponse = gson.fromJson(res, DataResponse.class);
                    callback.onFinish(true, dataResponse);
                } catch (JsonSyntaxException e) {
                    callback.onFinish(false, null);
                }
            }
        });
    }
}