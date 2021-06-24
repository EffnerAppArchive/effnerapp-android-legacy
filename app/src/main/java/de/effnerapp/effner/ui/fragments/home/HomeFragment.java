/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:20.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.ApiClient;
import de.effnerapp.effner.data.api.json.data.DataResponse;
import de.effnerapp.effner.tools.misc.ClassUtils;
import de.effnerapp.effner.tools.parse.HeaderTextParser;
import de.effnerapp.effner.tools.view.IntentHelper;

public class HomeFragment extends Fragment {

    private BottomNavigationView navView;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView headerTextView = view.findViewById(R.id.headerTextView);

        if(ApiClient.getInstance() == null) {
            requireActivity().recreate();
            return view;
        }

        DataResponse data = ApiClient.getInstance().getData();
        String headerText = new HeaderTextParser().parse(data.getHolidays(), data.getDayInformation());
        headerTextView.setText(headerText);

        String sClass = sharedPreferences.getString("APP_USER_CLASS", "");

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        navView = requireActivity().findViewById(R.id.nav_view);

        MaterialCardView timetableCard = view.findViewById(R.id.timetable_card);
        MaterialCardView substitutionsCard = view.findViewById(R.id.substitutions_card);
        MaterialCardView foodPlanCard = view.findViewById(R.id.food_plan_card);
        MaterialCardView newsCard = view.findViewById(R.id.news_card);
        MaterialCardView informationCard = view.findViewById(R.id.information_card);
        MaterialCardView mvvCard = view.findViewById(R.id.mvv_card);

        timetableCard.setOnClickListener(v -> navigateTo(R.id.navigation_timetable));

        // don't use navigateTo(id) here
        substitutionsCard.setOnClickListener(v -> navController.navigate(R.id.navigation_substitutions));

        foodPlanCard.setOnClickListener(v -> IntentHelper.openView(requireContext(), ApiClient.getInstance().getData().getContentByKey("DATA_FOOD_PLAN").getValue()));
        newsCard.setOnClickListener(v -> navigateTo(R.id.navigation_news));
        informationCard.setOnClickListener(v -> navigateTo(R.id.navigation_information));
        mvvCard.setOnClickListener(v -> navigateTo(R.id.navigation_mvv));

        return view;
    }

    private void navigateTo(@IdRes int id) {
        // workaround to uncheck the currently selected item
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        navView.getMenu().getItem(0).setCheckable(false);

        // navigate to news fragment
        navController.navigate(id);
    }
}