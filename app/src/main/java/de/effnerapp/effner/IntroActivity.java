package de.effnerapp.effner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

import de.effnerapp.effner.ui.login.LoginActivity;

public class IntroActivity extends AppIntro {

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        addSlide(AppIntroFragment.newInstance("title", "description", 0, Color.BLUE, Color.RED));
        addSlide(AppIntroFragment.newInstance("title2", "description2"));

        setTransformer(AppIntroPageTransformerType.Zoom.INSTANCE);
        setColorTransitionsEnabled(true);

        setIndicatorEnabled(true);
        setIndicatorColor(Color.RED, Color.GRAY);
        setProgressIndicator();

    }

    @Override
    protected void onSkipPressed(@org.jetbrains.annotations.Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        end();
    }

    @Override
    protected void onDonePressed(@org.jetbrains.annotations.Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        end();
    }

    /**
     * set COMPLETED_ON_BOARDING to true so you see the intro only once
     */

    public void setOnboarding() {
        sharedPreferences.edit().putBoolean("IntroActivity.COMPLETED_ON_BOARDING", true).apply();
        Log.d("splishsplash", "COMPLETED_ON_BOARDING set to " + sharedPreferences.getBoolean("IntroActivity.COMPLETED_ON_BOARDING", false));
    }


    /**
     * ends the onboadring process and returns either to the login activity or to the activity before.
     * also updates the preference
     */
    public void end() {
        boolean COMPLETED_ON_BOARDING = sharedPreferences.getBoolean("IntroActivity.COMPLETED_ON_BOARDING", false);
        setOnboarding();
        if (!COMPLETED_ON_BOARDING) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
