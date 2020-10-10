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
import com.github.appintro.AppIntroPageTransformerType;

import de.effnerapp.effner.ui.intro.IntroPageFactory;
import de.effnerapp.effner.ui.login.LoginActivity;

public class IntroActivity extends AppIntro {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        addSlide(new IntroPageFactory.Builder("Willkommen bei EffnerApp", "Ich hab doch selber keine Ahnung was ich da hin schreiben soll!")
                .imageDrawable(R.drawable.intro_logo)
                .backgroundDrawable(R.drawable.background_gradient)
                .build());

        addSlide(new IntroPageFactory.Builder("Immer up-to-date", "Mit der EffnerApp hast du immer den Überblick über deine Schulaufgaben, Vertretungen und vieles mehr!")
                .imageDrawable(R.drawable.intro_logo)
                .backgroundDrawable(R.drawable.background_gradient2)
                .build());

        addSlide(new IntroPageFactory.Builder("Los geht's", "Viel Spaß und so kp")
                .imageDrawable(R.drawable.intro_logo)
                .backgroundDrawable(R.drawable.background_gradient3)
                .build());

        setTransformer(AppIntroPageTransformerType.Zoom.INSTANCE);
//        setColorTransitionsEnabled(true);

        setIndicatorEnabled(true);
        setIndicatorColor(Color.RED, Color.GRAY);
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
        Log.d("Intro", "COMPLETED_ON_BOARDING set to " + sharedPreferences.getBoolean("IntroActivity.COMPLETED_ON_BOARDING", false));
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
