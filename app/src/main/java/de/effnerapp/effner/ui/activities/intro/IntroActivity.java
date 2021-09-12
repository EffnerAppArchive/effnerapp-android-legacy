/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 20:09.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.activities.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroPageTransformerType;

import de.effnerapp.effner.R;
import de.effnerapp.effner.ui.activities.login.LoginActivity;

public class IntroActivity extends AppIntro {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        addSlide(new IntroPageFactory.Builder(getString(R.string.intro_slide1_title), getString(R.string.intro_slide1_description))
                .imageDrawable(R.drawable.intro_logo)
                .backgroundDrawable(R.drawable.background_gradient)
                .build());

        addSlide(new IntroPageFactory.Builder(getString(R.string.intro_slide2_title), getString(R.string.intro_slide2_description))
                .imageDrawable(R.drawable.screenshot)
                .backgroundDrawable(R.drawable.background_gradient2)
                .build());

        addSlide(new IntroPageFactory.Builder(getString(R.string.intro_slide3_title), getString(R.string.intro_slide3_description))
                .imageDrawable(R.drawable.app_logo_intro)
                .backgroundDrawable(R.drawable.background_gradient3)
                .build());

        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
        setIndicatorEnabled(true);
        setProgressIndicator();
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        end();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        end();
    }

    /**
     * set COMPLETED_ON_BOARDING to true so you see the intro only once
     */
    public void setOnBoarding() {
        sharedPreferences.edit().putBoolean("IntroActivity.COMPLETED_ON_BOARDING", true).apply();
    }


    /**
     * ends the onBoarding process and returns either to the login activity or to the activity before.
     * also updates the preference
     */
    public void end() {
        boolean COMPLETED_ON_BOARDING = sharedPreferences.getBoolean("IntroActivity.COMPLETED_ON_BOARDING", false);
        setOnBoarding();
        if (!COMPLETED_ON_BOARDING) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
