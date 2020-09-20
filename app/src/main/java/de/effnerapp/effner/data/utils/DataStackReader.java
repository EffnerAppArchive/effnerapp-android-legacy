package de.effnerapp.effner.data.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.effnerapp.effner.data.DataStack;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataStackReader {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String BASE_URL = "https://api.effnerapp.de:45890/rest";
    private DataStack dataStack;
    private final Context context;
    private PackageInfo info;
    private final Activity activity;

    public DataStackReader(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public DataStack read(String sClass, String token) {
        OkHttpClient client = new OkHttpClient();

        Timer timer = new Timer();
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


        String url = BASE_URL + "?class=" + sClass + "&token=" + token + "&app_version=" + info.versionName;

        Request request = new Request.Builder()
                .url(url)
                .build();


        try {
            Response response = client.newCall(request).execute();
            String res = Objects.requireNonNull(response.body()).string();
            dataStack = gson.fromJson(res, DataStack.class);
            timer.cancel();
        } catch (IOException e) {
            e.printStackTrace();
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
