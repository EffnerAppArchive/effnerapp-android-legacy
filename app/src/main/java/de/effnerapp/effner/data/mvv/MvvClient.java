package de.effnerapp.effner.data.mvv;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MvvClient {
    private final Gson gson = new Gson();

    public void loadData(MvvCallback callback) {
        long time = System.currentTimeMillis() / 1000;
//        String stopId = "de%3A09174%3A7034";
        String stopId = "de:09174:6800"; //bahnhof
//        String stopId = "de:09174:7034"; // ??
//        String lines = "JmxpbmU9bXZ2JTNBNDA3MDIlM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBNDA3MDIlM0ElMjAlM0FSJTNBczIwJmxpbmU9bXZ2JTNBMTk3MDMlM0ElMjAlM0FSJTNBczIwJmxpbmU9bXZ2JTNBMTk3MDQlM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBMTk3MDQlM0ElMjAlM0FSJTNBczIwJmxpbmU9bXZ2JTNBNTc3MDUlM0ElMjAlM0FSJTNBczIwJmxpbmU9bXZ2JTNBMTk3MDYlM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBMTk3MDYlM0ElMjAlM0FSJTNBczIwJmxpbmU9bXZ2JTNBNDA3MTAlM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBNDA3MTAlM0ElMjAlM0FSJTNBczIwJmxpbmU9bXZ2JTNBNjU3MjAlM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBMTk3MjElM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBMTk3MjElM0ElMjAlM0FSJTNBczIwJmxpbmU9bXZ2JTNBNjU3MjIlM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBMTk3MjMlM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBMTk3MjMlM0ElMjAlM0FSJTNBczIwJmxpbmU9bXZ2JTNBMTk3MjUlM0ElMjAlM0FIJTNBczIwJmxpbmU9bXZ2JTNBMTk3MjUlM0ElMjAlM0FSJTNBczIw";
        String lines = "JmxpbmU9ZGRiJTNBOTJNMDIlM0ElMjAlM0FIJTNBajIwJmxpbmU9ZGRiJTNBOTJNMDIlM0ElMjAlM0FSJTNBajIwJmxpbmU9c3dtJTNBMDMxNzIlM0FHJTNBSCUzQTAxMCZsaW5lPW12diUzQTE5NzAzJTNBJTIwJTNBUiUzQXMyMCZsaW5lPW12diUzQTE5NzA0JTNBJTIwJTNBUiUzQXMyMCZsaW5lPW12diUzQTQwNzEwJTNBJTIwJTNBUiUzQXMyMCZsaW5lPW12diUzQTY1NzE3JTNBJTIwJTNBSCUzQXMyMCZsaW5lPW12diUzQTY1NzE4JTNBJTIwJTNBSCUzQXMyMCZsaW5lPW12diUzQTY1NzE5JTNBJTIwJTNBSCUzQXMyMCZsaW5lPW12diUzQTY1NzIwJTNBJTIwJTNBSCUzQXMyMCZsaW5lPW12diUzQTE5NzIxJTNBJTIwJTNBUiUzQXMyMCZsaW5lPW12diUzQTY1NzIyJTNBJTIwJTNBSCUzQXMyMCZsaW5lPW12diUzQTY1NzI2JTNBJTIwJTNBUiUzQXMyMCZsaW5lPW12diUzQTE5NzM2JTNBJTIwJTNBUiUzQXMyMCZsaW5lPW12diUzQTY1NzQ0JTNBJTIwJTNBUiUzQXMyMA%3D%3D";
        String url = "https://www.mvv-muenchen.de/" +
                "?eID=departuresFinder" +
                "&action=get_departures" +
                "&stop_id=" + stopId +
                "&requested_timestamp=" + time +
                "&lines=" + lines;

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
}
