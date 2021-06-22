/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 18:21.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.mvv;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import de.effnerapp.effner.data.mvv.json.FindStopResponse;
import de.effnerapp.effner.data.mvv.json.MvvResponse;
import de.effnerapp.effner.tools.misc.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MvvClient {
    private final Gson gson = new Gson();

    public void loadDepartures(String stopId, Consumer<MvvResponse> consumer) {
        long time = System.currentTimeMillis() / 1000;

        String url = "https://www.mvv-muenchen.de/" +
                "?eID=departuresFinder" +
                "&action=get_departures" +
                "&stop_id=" + stopId +
                "&requested_timestamp=" + time +
                // parameter 'lines' is required, but can be empty
                "&lines=";

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
                    MvvResponse data = gson.fromJson(res, MvvResponse.class);
                    consumer.accept(data);
                } catch (JsonSyntaxException e) {
                    // TODO: handle
                }
            }
        });
    }

    public FindStopResponse findStop(String query) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://www.mvv-muenchen.de/?eID=stopFinder&query=" + query)
                .build();

        try {
            Response response = client.newCall(request).execute();

            return gson.fromJson(Objects.requireNonNull(response.body()).string(), FindStopResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
