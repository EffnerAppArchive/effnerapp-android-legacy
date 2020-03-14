package de.effnerapp.effner.tools.auth;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import de.effnerapp.effner.json.Login;
import de.effnerapp.effner.tools.model.AuthError;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String url = "https://login.effnerapp.de/login";
    private static final MediaType JSON = MediaType.parse("application/json");
    private String res;
    private Login login;
    private PackageInfo info;
    private boolean ok;
    private AuthError error;

    public LoginManager(Context context) {
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String token) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = createRequestBody(token);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        Log.d("LoginMgr", "Logging in...");

        try {
            Response response = client.newCall(request).execute();
            res = Objects.requireNonNull(response.body()).string();
            login = gson.fromJson(res, Login.class);
            if (login.isLogin()) {
                ok = true;
            } else {
                error = new AuthError(true, login.getMsg());
                ok = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok;
    }

    private RequestBody createRequestBody(String token) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("token", token);
            postData.put("app_version", info.versionName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(postData.toString(), JSON);
    }

    public AuthError getError() {
        if(error == null) {
            error = new AuthError(false, null);
        }
        return error;
    }
}