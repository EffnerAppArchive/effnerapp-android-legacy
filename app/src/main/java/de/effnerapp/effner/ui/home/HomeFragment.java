package de.effnerapp.effner.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;

public class HomeFragment extends Fragment {

//    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textView = root.findViewById(R.id.text_home);
        String text;
        if(!SplashActivity.sharedPreferences.getString("APP_USER_USERNAME", "").isEmpty()) {
            text = "Hallo " + SplashActivity.sharedPreferences.getString("APP_USER_USERNAME", "") + "! Deine Klasse: "+ SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "ERROR");
        } else {
            text = "Hallo! Deine Klasse: "+ SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "ERROR");
        }

        textView.setText(text);
        return root;
    }
}