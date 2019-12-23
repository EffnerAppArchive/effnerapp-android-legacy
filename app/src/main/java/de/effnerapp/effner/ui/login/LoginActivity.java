package de.effnerapp.effner.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText effnerappID = findViewById(R.id.effnerapp_id);
        EditText password = findViewById(R.id.password);
        Spinner sClass = findViewById(R.id.sClass);
        EditText username = findViewById(R.id.username);
        Button loginButton = findViewById(R.id.login);
        ProgressBar loadingProgressBar = findViewById(R.id.loading);
        List<String> items = new ArrayList<>(getClasses());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        sClass.setAdapter(adapter);
        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            if(!effnerappID.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                LoginManager loginManager = new LoginManager();
                boolean login = loginManager.register(effnerappID.getText().toString(), password.getText().toString(), sClass.getSelectedItem().toString(), username.getText().toString());
                if(login) {
                    Toast.makeText(this, "Du hast dich erfolgreich angemeldet!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Fehler beim Anmelden!", Toast.LENGTH_LONG).show();
                }
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
                    String res = response.body().string();
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
