/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 20:09.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.activities.login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import de.effnerapp.effner.R;
import de.effnerapp.effner.ui.activities.intro.IntroActivity;

public class TeacherLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_teacher);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        EditText id = findViewById(R.id.input_id);
        EditText password = findViewById(R.id.input_password);
        EditText teacher = findViewById(R.id.input_teacher_id);
        Button loginButton = findViewById(R.id.login_button);

        TextView backToIntroButton = findViewById(R.id.back_to_intro_link);
        TextView studentLoginButton = findViewById(R.id.student_login_link);

        backToIntroButton.setOnClickListener(v -> startActivity(new Intent(this, IntroActivity.class)));
        studentLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        loginButton.setOnClickListener(v -> Snackbar.make(findViewById(R.id.container), "Diese Funktion ist derzeit noch nicht verfügbar!", BaseTransientBottomBar.LENGTH_LONG).show());
    }
}