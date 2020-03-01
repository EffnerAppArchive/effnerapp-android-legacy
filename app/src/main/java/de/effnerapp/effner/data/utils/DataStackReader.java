package de.effnerapp.effner.data.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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
    private Context context;
    private Activity activity;

    public DataStackReader(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public DataStack read(String sClass, String token) {
        Timer timer = new Timer();
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.effnerapp.de/rest/v2/get.php" + "?class=" + sClass + "&token=" + token;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                res = Objects.requireNonNull(response.body()).string();
                System.out.println(res);
                dataStack = gson.fromJson(res, DataStack.class);
                timer.cancel();
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
                timer.cancel();
                handleError();
            }
        });

        timer.schedule(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                if (i >= 10) {
                    Log.e("DataStackReader", "Connection timed out! [TIMER]");
                    handleError();
                    client.dispatcher().cancelAll();
                    timer.cancel();
                    return;
                }
                i++;
            }
        }, 0, 1000);

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

    private void handleError() {
        Log.e("DataStackReader", "Connection timed out!");
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Verbindung fehlgeschlagen!")
                .setCancelable(false)
                .setMessage("Die Verbindung zu unseren Servern ist fehlgeschlagen! Bitte überprüfe deine Internetverbindung oder versuche es später erneut!")
                .setNegativeButton("Neu Versuchen", (dialogInterface, i) -> activity.recreate())
                .setPositiveButton("Ok", (dialogInterface, i) -> activity.finish());
        activity.runOnUiThread(builder::show);
    }
}
