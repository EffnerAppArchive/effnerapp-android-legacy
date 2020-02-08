package de.effnerapp.effner.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.json.Error;
import de.effnerapp.effner.json.Login;
import de.effnerapp.effner.json.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LoginManager {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private HashGenerator hashGenerator = new HashGenerator("SHA-256", UTF_8);
    private User user;
    private Login login;
    private Context context;
    private Activity activity;
    private PackageInfo info;
    private boolean isError;

    public LoginManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean register(String id, String password, String sClass, String username) {
        OkHttpClient client = new OkHttpClient();
        Timer timer = new Timer();
        final String[] res = new String[1];
        final Boolean[] ok = new Boolean[1];

        new Thread(() -> {
            String url = buildUrl(id, password, sClass, username);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    res[0] = Objects.requireNonNull(response.body()).string();

                    Error error = gson.fromJson(res[0], Error.class);
                    if(error.getError() != null && !error.getError().isEmpty()) {
                        isError = true;
                        timer.cancel();
                        ok[0] = false;
                    } else {
                        isError = false;
                        timer.cancel();
                        ok[0] = true;
                        user = gson.fromJson(res[0], User.class);
                    }

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("LoginMgr", "Exception: " + e);
                    ok[0] = false;
                    isError = true;
                }
            });
        }).start();

        timer.schedule(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if(i >= 10) {
                     Log.e("LoginMgr", "Connection timed out!");
                     AlertDialog.Builder builder =  new AlertDialog.Builder(context)
                            .setTitle("Verbindung fehlgeschlagen!")
                            .setCancelable(false)
                            .setMessage("Die Verbindung zu unseren Servern ist fehlgeschlagen! Bitte überprüfe deine Internetverbindung oder versuche es später erneut!")
                            .setNegativeButton("Neu Versuchen", (dialogInterface, i) -> {
                                for(Call call : client.dispatcher().queuedCalls()) {
                                    call.cancel();
                                }
                                activity.recreate();
                            })
                            .setPositiveButton("Ok", (dialogInterface, i) -> {
                                for(Call call : client.dispatcher().queuedCalls()) {
                                    call.cancel();
                                }
                                activity.finish();
                            });
                     activity.runOnUiThread(builder::show);
                     isError = true;
                     timer.cancel();
                     return;
                }
                i++;
            }
        },0,1000);

        while (res[0] == null && ok[0] == null) {
            Log.d("LoginMgr", "Waiting for Server...");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(ok[0]) {
            //put sharedPreference Values
            SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
            editor.putBoolean("APP_REGISTERED", true);
            editor.putString("APP_AUTH_TOKEN", user.getToken());
            editor.putString("APP_USER_CLASS", user.getsClass());
            editor.putString("APP_DSB_LOGIN_ID", id);
            editor.putString("APP_DSB_LOGIN_PASSWORD", password);
            if(user.getUsername() != null && !user.getUsername().isEmpty()) {
                editor.putString("APP_USER_USERNAME", user.getUsername());
            }
            editor.apply();
            //enable general notifications
            FirebaseMessaging.getInstance().subscribeToTopic("APP_GENERAL_NOTIFICATIONS");
        }

        return ok[0];
    }

    public boolean login(String token) {
        OkHttpClient client = new OkHttpClient();
        Timer timer = new Timer();
        final String[] res = new String[1];
        final Boolean[] ok = new Boolean[1];

        new Thread(() -> {
            Log.d("LoginMgr", "Logging in...");

            String url = "https://login.effnerapp.de/login" + "?token=" + token + "&app_version=" + info.versionName;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    res[0] = Objects.requireNonNull(response.body()).string();

                    Error error = gson.fromJson(res[0], Error.class);
                    if(error.getError() != null && !error.getError().isEmpty()) {
                        ok[0] = false;
                        isError = true;
                        timer.cancel();
                        AlertDialog.Builder builder =  new AlertDialog.Builder(context)
                                .setTitle("Anmeldevorgang fehlgeschlagen!")
                                .setCancelable(false)
                                .setMessage("Error: " + error.getError())
                                .setNegativeButton("Neu Versuchen", (dialogInterface, i) -> call.cancel())
                                .setPositiveButton("Ok", (dialogInterface, i) -> {
                                    call.cancel();
                                    activity.recreate();
                                });
                        activity.runOnUiThread(builder::show);

                    } else {
                        isError = false;
                        timer.cancel();
                        ok[0] = true;
                        login = gson.fromJson(res[0], Login.class);
                        ok[0] = login.isLogin();
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("LoginMgr", "Exception: " + e);
                    isError = true;
                    ok[0] = false;
                }
            });
        }).start();

        timer.schedule(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if(i >= 10) {
                    Log.e("LoginMgr", "Connection timed out!");
                    AlertDialog.Builder builder =  new AlertDialog.Builder(context)
                            .setTitle("Verbindung fehlgeschlagen!")
                            .setCancelable(false)
                            .setMessage("Die Verbindung zu unseren Servern ist fehlgeschlagen! Bitte überprüfe deine Internetverbindung oder versuche es später erneut!")
                            .setNegativeButton("Neu Versuchen", (dialogInterface, i) -> {
                                for(Call call : client.dispatcher().queuedCalls()) {
                                    call.cancel();
                                }
                                activity.recreate();
                            })
                            .setPositiveButton("Ok", (dialogInterface, i) -> {
                                for(Call call : client.dispatcher().queuedCalls()) {
                                    call.cancel();
                                }
                                activity.finish();
                            });
                    activity.runOnUiThread(builder::show);
                    isError = true;
                    timer.cancel();
                    return;
                }
                i++;
            }
        },0,1000);

        while (res[0] == null && ok[0] == null) {
            Log.d("LoginMgr", "Waiting for Server...");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return ok[0];
    }

    private String buildUrl(String id, String password, String sClass, String username) {
        String url = null;
        String firebaseToken;
        if(SplashActivity.sharedPreferences.getString("APP_FIREBASE_TOKEN", "").isEmpty()) {
            firebaseToken = FirebaseInstanceId.getInstance().getToken();
            SplashActivity.sharedPreferences.edit().putString("APP_FIREBASE_TOKEN", firebaseToken).apply();
        } else {
            firebaseToken = SplashActivity.sharedPreferences.getString("APP_FIREBASE_TOKEN", "");
        }
        try {
            url = "https://login.effnerapp.de/register" + "?id=" + hashGenerator.generate(id) + "&password=" + hashGenerator.generate(password) + "&class=" + sClass + "&firebase_token=" + firebaseToken + "&app_version=" + info.versionName;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(username != null && !username.isEmpty()) {
            url += "&username=" + username;
        }

        return url;
    }

    public boolean isError() {
        return isError;
    }
}
