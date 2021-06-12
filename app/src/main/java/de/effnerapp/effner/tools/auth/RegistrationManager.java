package de.effnerapp.effner.tools.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;


import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.json.Auth;
import de.effnerapp.effner.json.AuthResponse;
import de.effnerapp.effner.services.Authenticator;
import de.effnerapp.effner.tools.HashGenerator;
import de.effnerapp.effner.tools.model.AuthError;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegistrationManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Context context;
    private Auth auth;
    private boolean ok;
    private AuthError error;

    public RegistrationManager(Context context) {
        this.context = context;
    }

    // TODO: async
    public boolean register(String id, String password, String sClass) {

        // RESET FIREBASE INSTANCE (Reset Topics)

        // don't know if this works
        FirebaseInstallations.getInstance().delete();

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(createUrl(id, password, sClass))
                .build();

        try {
            Response response = client.newCall(request).execute();
            String res = Objects.requireNonNull(response.body()).string();
            AuthResponse authResponse = gson.fromJson(res, AuthResponse.class);

            if (authResponse.getStatus().getStatus() == 200) {
                auth = authResponse.getAuth();
                ok = auth.getToken() != null;
            } else {
                error = new AuthError(true, authResponse.getStatus().getMsg());
                ok = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ok) {
            addAccount(id, password);
        }

        return ok;
    }

    private String createUrl(String id, String password, String sClass) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String firebaseToken = sharedPreferences.getString("APP_FIREBASE_TOKEN", "NONE");
        HashGenerator hashGenerator = new HashGenerator("SHA-512", StandardCharsets.UTF_8);

        return context.getString(R.string.uri_api_register) +
                "?id=" + hashGenerator.generate(id) +
                "&password=" + hashGenerator.generate(password) +
                "&class=" + sClass +
                "&firebase_token=" + firebaseToken;
    }


    private void addAccount(String id, String password) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        AccountManager accountManager = AccountManager.get(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("APP_REGISTERED", true);
        editor.putString("APP_DSB_LOGIN_ID", id);
        editor.putString("APP_DSB_LOGIN_PASSWORD", password);
        editor.putString("APP_USER_CLASS", auth.getsClass());
        editor.apply();

        //enable general notifications
        FirebaseMessaging.getInstance().subscribeToTopic("APP_GENERAL_NOTIFICATIONS");

        for (Account account : accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)) {
            accountManager.removeAccountExplicitly(account);
        }

        Account account = new Account(Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, auth.getToken(), null);
    }

    public AuthError getError() {
        if (error == null) {
            error = new AuthError(false, null);
        }
        return error;
    }
}
