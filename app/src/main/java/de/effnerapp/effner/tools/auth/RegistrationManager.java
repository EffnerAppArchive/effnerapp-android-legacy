package de.effnerapp.effner.tools.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.json.Status;
import de.effnerapp.effner.json.User;
import de.effnerapp.effner.services.Authenticator;
import de.effnerapp.effner.tools.HashGenerator;
import de.effnerapp.effner.tools.model.AuthError;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String url = "https://login.effnerapp.de/register";
    private static final MediaType JSON = MediaType.parse("application/json");
    private Context context;
    private User user;
    private Boolean ok;
    private AuthError error;

    public RegistrationManager(Context context) {
        this.context = context;
    }

    public boolean register(String id, String password, String sClass, String username) {
        // RESET FIREBASE INSTANCE (Reset Topics)
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = createRequestBody(id, password, sClass, username);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        Log.d("RegMgr", "Logging in...");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();
                Status status = gson.fromJson(res, Status.class);

                System.out.println(res);
                if (status.getStatus() == null) {
                    user = gson.fromJson(res, User.class);
                    ok = user.getToken() != null;
                } else {
                    error = new AuthError(true, status.getMsg());
                    ok = false;
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("RegMgr", "Exception: " + e);
                error = new AuthError(true, e.getMessage());
                ok = false;
            }
        });

        while (user == null && error == null) {
            Log.d("RegMgr", "Waiting for Server...");
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {

            }
        }

        if (ok) {
            addAccount(id, password);
        }

        return ok;
    }

    private RequestBody createRequestBody(String id, String password, String sClass, String username) {
        String firebaseToken = SplashActivity.sharedPreferences.getString("APP_FIREBASE_TOKEN", "NONE");
        HashGenerator hashGenerator =  new HashGenerator("SHA-512", StandardCharsets.UTF_8);
        JSONObject postData = new JSONObject();
        try {
            postData.put("id", hashGenerator.generate(id));
            postData.put("password", hashGenerator.generate(password));
            postData.put("class", sClass);
            postData.put("firebase_token", firebaseToken);
            if(!username.isEmpty()) {
                postData.put("username", username);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(postData.toString(), JSON);
    }

    private void addAccount(String id, String password) {
        AccountManager accountManager = AccountManager.get(context);

        SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
        editor.putBoolean("APP_REGISTERED", true);
        editor.putString("APP_DSB_LOGIN_ID", id);
        editor.putString("APP_DSB_LOGIN_PASSWORD", password);
        editor.putString("APP_USER_CLASS", user.getsClass());

        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            editor.putString("APP_USER_USERNAME", user.getUsername());
        }
        editor.apply();
        //enable general notifications
        FirebaseMessaging.getInstance().subscribeToTopic("APP_GENERAL_NOTIFICATIONS");

        for(Account account : accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)) {
            accountManager.removeAccountExplicitly(account);
        }

        Account account = new Account(Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, user.getToken(), null);
    }

    public AuthError getError() {
        if(error == null) {
            error = new AuthError(false, null);
        }
        return error;
    }
}
