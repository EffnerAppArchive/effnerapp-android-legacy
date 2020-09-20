package de.effnerapp.effner.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.TimetableActivity;
import de.effnerapp.effner.data.DataStack;
import de.effnerapp.effner.data.utils.HeaderTextParser;
import de.effnerapp.effner.tools.ClassUtils;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView headerTextView = view.findViewById(R.id.headerTextView);
        DataStack dataStack = SplashActivity.getDataStack();
        String headerText = new HeaderTextParser().parse(dataStack.getHolidays(), dataStack.getDayInformation(), SplashActivity.sharedPreferences.getString("APP_USER_USERNAME", ""));
        headerTextView.setText(headerText);

        String sClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        BottomNavigationView navView = view.findViewById(R.id.nav_view);

        MaterialCardView timetableCard = view.findViewById(R.id.timetable_card);
        MaterialCardView illnessDocCard = view.findViewById(R.id.illness_doc_card);
        MaterialCardView foodPlanCard = view.findViewById(R.id.food_plan_card);
        MaterialCardView substitutionCard = view.findViewById(R.id.subs_card);
        MaterialCardView newsCard = view.findViewById(R.id.news_card);
        MaterialCardView customCard = view.findViewById(R.id.custom_card);

        timetableCard.setOnClickListener(v -> startActivity(new Intent(getContext(), TimetableActivity.class)));
        illnessDocCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SplashActivity.getDataStack().getContentByKey("DATA_ILLNESS_DOC_" + (ClassUtils.isAdvancedClass(sClass) ? 1 : 0)).getValue()))));
        foodPlanCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SplashActivity.getDataStack().getContentByKey("DATA_FOOD_PLAN").getValue()))));
        substitutionCard.setOnClickListener(v -> navController.navigate(R.id.navigation_substitutions));
        if(SplashActivity.getDataStack().getContentByKey("DATA_CUSTOM") != null) {
            customCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SplashActivity.getDataStack().getContentByKey("DATA_CUSTOM").getValue()))));
        } else {
            customCard.setVisibility(View.GONE);
        }


        newsCard.setOnClickListener(v -> navController.navigate(R.id.navigation_news));

        return view;
    }
}