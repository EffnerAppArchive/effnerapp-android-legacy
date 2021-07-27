/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:46.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.activities.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.tools.auth.ServerAuthenticator;
import de.effnerapp.effner.tools.error.ErrorUtils;
import de.effnerapp.effner.tools.misc.ClassUtils;
import de.effnerapp.effner.tools.misc.Promise;
import de.effnerapp.effner.ui.activities.intro.IntroActivity;
import de.effnerapp.effner.ui.activities.splash.SplashActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient();

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

        ImageView helpButton = findViewById(R.id.help_button);

        TextView privacyWarning = findViewById(R.id.warning_privacy);
        privacyWarning.setText(Html.fromHtml(getString(R.string.text_warning_privacy)));
        privacyWarning.setMovementMethod(LinkMovementMethod.getInstance());

        helpButton.setOnClickListener(v -> {
            hideKeyboardFrom(this, findViewById(R.id.container));
            Snackbar.make(helpButton, getString(R.string.s_login_help), BaseTransientBottomBar.LENGTH_LONG).show();
        });


        String url = getString(R.string.uri_api_get_classes);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();

                try {
                    List<String> items = new ArrayList<>(Arrays.asList(gson.fromJson(res, String[].class)));
                    items.addAll(Arrays.asList("11", "12"));
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_list_item_1, items);
                    runOnUiThread(() -> classSelector.setAdapter(adapter));
                } catch (IllegalStateException | JsonSyntaxException e) {
                    Snackbar.make(findViewById(R.id.container), R.string.s_err_load_classes, BaseTransientBottomBar.LENGTH_INDEFINITE).setAction(R.string.button_retry, v -> recreate()).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Snackbar.make(findViewById(R.id.container), R.string.s_err_load_classes, BaseTransientBottomBar.LENGTH_INDEFINITE).setAction(R.string.button_retry, v -> recreate()).show();
            }
        });

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
                dialog.setTitle(R.string.d_login_progress_title);
                dialog.setMessage(getString(R.string.d_login_progress_message));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();

                String sClass = ClassUtils.isAdvancedClass(classSelector.getSelectedItem().toString()) ? classSelector.getSelectedItem().toString() + "Q" + course.getText().toString() : classSelector.getSelectedItem().toString();

                ServerAuthenticator serverAuthenticator = new ServerAuthenticator(this);
                serverAuthenticator.login(id.getText().toString(), password.getText().toString(), sClass, new Promise<Void, String>() {
                    @Override
                    public void accept(Void unused) {
                        runOnUiThread(() -> {
                            dialog.cancel();
                            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                            finish();
                        });
                    }

                    @Override
                    public void reject(String error) {
                        System.err.println(error);
                        runOnUiThread(dialog::cancel);
                        if (error.equals("Unauthorized")) {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, R.string.s_err_server_authentication, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        ErrorUtils.with(LoginActivity.this).showError(error, false, true);
                    }
                });
            } else {
                Snackbar.make(findViewById(R.id.container), R.string.s_prompt_enter_credentials, BaseTransientBottomBar.LENGTH_LONG).show();
                hideKeyboardFrom(this, findViewById(R.id.container));
            }
        });

        backToIntroButton.setOnClickListener(v -> startActivity(new Intent(this, IntroActivity.class)));
        teacherLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TeacherLoginActivity.class));
            finish();
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
