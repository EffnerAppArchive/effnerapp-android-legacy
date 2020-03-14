package de.effnerapp.effner.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.TimetableActivity;
import de.effnerapp.effner.data.DataStack;
import de.effnerapp.effner.data.utils.HeaderTextParser;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView headerTextView = view.findViewById(R.id.headerTextView);
        DataStack dataStack = SplashActivity.getDataStack();
        String headerText = new HeaderTextParser().parse(dataStack.getHolidays(), dataStack.getPhday(), SplashActivity.sharedPreferences.getString("APP_USER_USERNAME", ""));
        headerTextView.setText(headerText);

        String sClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");

        NavController navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        BottomNavigationView navView = view.findViewById(R.id.nav_view);

        CardView timetableCard = view.findViewById(R.id.timetable_card);
        CardView illnessDocCard = view.findViewById(R.id.illness_doc_card);
        CardView foodPlanCard = view.findViewById(R.id.food_plan_card);
        CardView substitutionCard = view.findViewById(R.id.subs_card);
        CardView newsCard = view.findViewById(R.id.news_card);

        timetableCard.setOnClickListener(v -> startActivity(new Intent(getContext(), TimetableActivity.class)));
        illnessDocCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SplashActivity.getDataStack().getContentByKey("DATA_ILLNESS_DOC_" + ((sClass.startsWith("11") || sClass.startsWith("12")) ? 1 : 0)).getValue()))));
        foodPlanCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SplashActivity.getDataStack().getContentByKey("DATA_FOOD_PLAN").getValue()))));
        substitutionCard.setOnClickListener(v -> {
            navController.navigate(R.id.navigation_substitutions);
        });

        newsCard.setOnClickListener(v -> {
            navController.navigate(R.id.navigation_news);
        });

        return view;
    }
}