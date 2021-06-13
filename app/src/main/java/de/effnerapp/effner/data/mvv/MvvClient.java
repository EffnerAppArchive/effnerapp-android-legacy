package de.effnerapp.effner.data.mvv;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import de.effnerapp.effner.data.mvv.callbacks.MvvCallback;
import de.effnerapp.effner.data.mvv.json.FindStopResponse;
import de.effnerapp.effner.data.mvv.json.MvvResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MvvClient {
    private final Gson gson = new Gson();

    public void loadDepartures(String stopId, MvvCallback callback) {
        long time = System.currentTimeMillis() / 1000;

        String url = "https://www.mvv-muenchen.de/" +
                "?eID=departuresFinder" +
                "&action=get_departures" +
                "&stop_id=" + stopId +
                "&requested_timestamp=" + time +
                // parameter 'lines' is required, but can be empty
                "&lines=";

        OkHttpClient client = new OkHttpClient();

        System.out.println(url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                callback.onFinish(false, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();
                System.out.println(res);
                try {
                    MvvResponse mvvResponse = gson.fromJson(res, MvvResponse.class);
                    callback.onFinish(true, mvvResponse);
                } catch (JsonSyntaxException e) {
                    callback.onFinish(false, null);
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
