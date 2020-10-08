package de.effnerapp.effner.ui.login;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.json.Classes;
import de.effnerapp.effner.tools.ClassUtils;
import de.effnerapp.effner.tools.auth.ServerAuthenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Classes classes;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText effnerappID = findViewById(R.id.effnerapp_id);
        EditText password = findViewById(R.id.password);
        Spinner classSelector = findViewById(R.id.sClass);
        EditText course = findViewById(R.id.course);
        EditText username = findViewById(R.id.username);
        Button loginButton = findViewById(R.id.login);
        List<String> items = new ArrayList<>(getClasses());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        classSelector.setAdapter(adapter);
        AccountManager accountManager = AccountManager.get(this);
        if (accountManager.getAccountsByType("de.effnerapp").length == 1) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Ein Account existiert bereits!")
                    .setMessage("Auf deinem Gerät befindet sich bereits ein EffnerApp-Account, jedoch schlug die Anmeldung am Server fehl!" +
                            "\nDu kannst es entweder erneut versuchen oder dich neu anmelden!")
                    .setPositiveButton("Neu versuchen", (dialogInterface, i) -> {
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    })
                    .setNegativeButton("Neu Anmelden", (dialogInterface, i) -> Toast.makeText(this, "Bitte melde dich an!", Toast.LENGTH_SHORT).show());
            dialog.show();
        } else {
            Toast.makeText(this, "Bitte melde dich an!", Toast.LENGTH_SHORT).show();
        }

        classSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ClassUtils.isAdvancedClass(classSelector.getItemAtPosition(position).toString())) {
                    course.setVisibility(View.VISIBLE);
                } else {
                    course.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                course.setVisibility(View.INVISIBLE);
            }
        });

        loginButton.setOnClickListener(v -> {
            if (!effnerappID.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && (ClassUtils.isAdvancedClass(classSelector.getSelectedItem().toString()) && !course.getText().toString().isEmpty()) || (!ClassUtils.isAdvancedClass(classSelector.getSelectedItem().toString()))) {
                dialog = new ProgressDialog(this);
                dialog.setTitle("Prüfe Daten...");
                dialog.setMessage("Die Anmeldedaten werden überprüft!");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
                new Thread(() -> {
                    ServerAuthenticator serverAuthenticator = new ServerAuthenticator(this);
                    String sClass = ClassUtils.isAdvancedClass(classSelector.getSelectedItem().toString()) ? classSelector.getSelectedItem().toString() + "Q" + course.getText().toString() : classSelector.getSelectedItem().toString();
                    boolean login = serverAuthenticator.register(effnerappID.getText().toString(), password.getText().toString(), sClass, username.getText().toString());
                    runOnUiThread(dialog::cancel);
                    if (login) {
                        runOnUiThread(() -> Toast.makeText(this, "Du hast dich erfolgreich angemeldet!", Toast.LENGTH_LONG).show());
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    } else {
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
                    classes = gson.fromJson(res, Classes.class);
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("LA", "Fail! " + e.getMessage());
                }

            });
        }).start();


        while (classes == null) {
            Log.d("LA", "Getting classes");
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
