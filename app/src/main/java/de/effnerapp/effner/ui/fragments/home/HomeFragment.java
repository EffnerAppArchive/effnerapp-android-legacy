/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.fragments.home;

import android.content.Intent;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.ApiClient;
import de.effnerapp.effner.data.api.json.data.DataResponse;
import de.effnerapp.effner.tools.view.IntentHelper;
import de.effnerapp.effner.ui.activities.splash.SplashActivity;

public class HomeFragment extends Fragment {

    private BottomNavigationView navView;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if(ApiClient.getInstance() == null) {
            startActivity(new Intent(requireContext(), SplashActivity.class));
            requireActivity().finish();
            return view;
        }

        DataResponse data = ApiClient.getInstance().getData();

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        navView = requireActivity().findViewById(R.id.nav_view);

        TextView motd = view.findViewById(R.id.motd);
        MaterialCardView timetableCard = view.findViewById(R.id.timetable_card);
        MaterialCardView substitutionsCard = view.findViewById(R.id.substitutions_card);
        MaterialCardView foodPlanCard = view.findViewById(R.id.food_plan_card);
        MaterialCardView informationCard = view.findViewById(R.id.information_card);
        MaterialCardView mvvCard = view.findViewById(R.id.mvv_card);

        motd.setText(data.getMotd());

        timetableCard.setOnClickListener(v -> navigateTo(R.id.navigation_timetable));

        // don't use navigateTo(id) here
        substitutionsCard.setOnClickListener(v -> navController.navigate(R.id.navigation_substitutions));

        foodPlanCard.setOnClickListener(v -> IntentHelper.openView(requireContext(), ApiClient.getInstance().getData().getDocumentByKey("DATA_FOOD_PLAN").getUri()));
        informationCard.setOnClickListener(v -> navigateTo(R.id.navigation_information));
        mvvCard.setOnClickListener(v -> navigateTo(R.id.navigation_mvv));

        return view;
    }

    private void navigateTo(@IdRes int id) {
        // workaround to uncheck the currently selected item
        navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
        navView.getMenu().getItem(0).setCheckable(false);

        // navigate to fragment
        navController.navigate(id);
    }
}