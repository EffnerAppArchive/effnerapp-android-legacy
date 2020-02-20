package de.effnerapp.effner.data.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import de.effnerapp.effner.data.DataStack;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataStackReader {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private DataStack dataStack;
    private String res = null;

    public DataStackReader() {

    }

    public DataStack read(String sClass, String token) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            String url = "https://api.effnerapp.de/rest/v2/get.php" + "?class=" + sClass + "&token=" + token;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    res = response.body().string();
                    System.out.println(res);
                    dataStack = gson.fromJson(res, DataStack.class);
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
            });

        }).start();

        while (res == null) {
            Log.d("DataStackReader", "Fetching Data!");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return dataStack;
    }
}
