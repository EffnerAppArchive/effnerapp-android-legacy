package de.effnerapp.effner.ui.activities.login;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.utils.ClassesCallback;
import de.effnerapp.effner.services.Authenticator;
import de.effnerapp.effner.tools.ClassUtils;
import de.effnerapp.effner.tools.auth.ServerAuthenticator;
import de.effnerapp.effner.ui.activities.intro.IntroActivity;
import de.effnerapp.effner.ui.activities.splash.SplashActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText id = findViewById(R.id.input_id);
        EditText password = findViewById(R.id.input_password);
        Spinner classSelector = findViewById(R.id.input_class);
        EditText course = findViewById(R.id.input_course);
        Button loginButton = findViewById(R.id.login_button);

        TextView backToIntroButton = findViewById(R.id.back_to_intro_link);
        TextView teacherLoginButton = findViewById(R.id.teacher_login_link);


        getClasses(new ClassesCallback() {
            @Override
            public void onSuccess(List<String> classes) {
                List<String> items = new ArrayList<>(classes);
                items.addAll(Arrays.asList("11", "12"));
                ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_list_item_1, items);
                runOnUiThread(() -> classSelector.setAdapter(adapter));
            }

            @Override
            public void onFailure() {
                Snackbar.make(findViewById(R.id.container), "Fehler beim Laden der Klassen.", BaseTransientBottomBar.LENGTH_LONG).setAction("Retry", v -> recreate()).show();
            }
        });

        AccountManager accountManager = AccountManager.get(this);
        if (accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE).length == 1) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Ein Account existiert bereits!")
                    .setMessage("Auf deinem Gerät befindet sich bereits ein EffnerApp-Account, jedoch schlug die Anmeldung am Server fehl!" +
                            "\nDu kannst es entweder erneut versuchen oder dich neu anmelden!")
                    .setPositiveButton("Neu versuchen", (dialogInterface, i) -> {
                        System.err.println("Starting splash");
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    })
                    .setNegativeButton("Neu Anmelden", (dialogInterface, i) -> Toast.makeText(this, "Bitte melde dich an!", Toast.LENGTH_SHORT).show());
            dialog.show();
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
            if (validateInput(id.getText().toString(), password.getText().toString(), classSelector.getSelectedItem(), course.getText().toString())) {
                dialog = new ProgressDialog(this);
                dialog.setTitle("Anmeldung ...");
                dialog.setMessage("Die Anmeldedaten werden überprüft!");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
                new Thread(() -> {
                    ServerAuthenticator serverAuthenticator = new ServerAuthenticator(this);
                    String sClass = ClassUtils.isAdvancedClass(classSelector.getSelectedItem().toString()) ? classSelector.getSelectedItem().toString() + "Q" + course.getText().toString() : classSelector.getSelectedItem().toString();
                    boolean login = serverAuthenticator.register(id.getText().toString(), password.getText().toString(), sClass);
                    runOnUiThread(dialog::cancel);
                    if (login) {
                        runOnUiThread(() -> Toast.makeText(this, "Anmeldung erfolgreich!", Toast.LENGTH_SHORT).show());
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Fehler beim Anmelden!", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            } else {
                Snackbar.make(findViewById(R.id.container), "Bitte gebe die Anmeldedaten ein!", BaseTransientBottomBar.LENGTH_LONG).show();
                hideKeyboardFrom(this, findViewById(R.id.container));
            }
        });

        backToIntroButton.setOnClickListener(v -> startActivity(new Intent(this, IntroActivity.class)));
        teacherLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TeacherLoginActivity.class));
            finish();
        });

    }

    private void getClasses(ClassesCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.effnerapp.de:45890/data/classes/get";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();
                callback.onSuccess(Arrays.asList(gson.fromJson(res, String[].class)));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure();
            }
        });
    }

    private boolean validateInput(String id, String password, Object sClass, String course) {
        if (sClass == null) return false;

        if (ClassUtils.isAdvancedClass(sClass.toString())) {
            return validate(id, password, sClass.toString(), course);
        } else {
            return validate(id, password, sClass.toString());
        }
    }

    private boolean validate(String... input) {
        for (String s : input) {
            if (s == null || s.isEmpty()) return false;
        }
        return true;
    }

    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
