package de.effnerapp.effner.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.json.Classes;
import de.effnerapp.effner.tools.LoginManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Classes classes;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText effnerappID = findViewById(R.id.effnerapp_id);
        EditText password = findViewById(R.id.password);
        Spinner sClass = findViewById(R.id.sClass);
        EditText username = findViewById(R.id.username);
        Button loginButton = findViewById(R.id.login);
        List<String> items = new ArrayList<>(getClasses());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        sClass.setAdapter(adapter);
        loginButton.setOnClickListener(v -> {
            if(!effnerappID.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                dialog = new ProgressDialog(this);
                dialog.setTitle("Prüfe Daten...");
                dialog.setMessage("Die Anmeldedaten werden überprüft!");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
                new Thread(() -> {
                    LoginManager loginManager = new LoginManager(this);
                    boolean login = false;
                    try {
                        login = loginManager.register(effnerappID.getText().toString(), password.getText().toString(), sClass.getSelectedItem().toString(), username.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    if(login) {
                        runOnUiThread(() -> Toast.makeText(this, "Du hast dich erfolgreich angemeldet!", Toast.LENGTH_LONG).show());
                        dialog.cancel();
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    } else {
                        dialog.cancel();
                        runOnUiThread(() -> Toast.makeText(this, "Fehler beim Anmelden!", Toast.LENGTH_LONG).show());
                    }
                }).start();
            } else {
                Toast.makeText(this, "Bitte gebe die Anmeldedaten ein!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<String> getClasses() {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            String url = "https://api.effnerapp.de/classes/get.php";

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String res = Objects.requireNonNull(response.body()).string();
                    System.out.println(res);
                    classes = gson.fromJson(res, Classes.class);
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("LA", "Fail! " + e.getMessage());
                }
            });
        }).start();


        while (classes == null) {
            System.out.println("Getting classes");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String[] classArray = classes.getClasses().split(";");
        return Arrays.asList(classArray);
    }
}
