package de.effnerapp.effner.ui.fragments.home;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.model.DataResponse;
import de.effnerapp.effner.data.utils.ApiClient;
import de.effnerapp.effner.data.utils.HeaderTextParser;
import de.effnerapp.effner.tools.ClassUtils;
import de.effnerapp.effner.ui.activities.timetable.TimetableActivity;

public class HomeFragment extends Fragment {

    private BottomNavigationView navView;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView headerTextView = view.findViewById(R.id.headerTextView);
        DataResponse data = ApiClient.getInstance().getData();
        String headerText = new HeaderTextParser().parse(data.getHolidays(), data.getDayInformation());
        headerTextView.setText(headerText);

        String sClass = sharedPreferences.getString("APP_USER_CLASS", "");

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        navView = requireActivity().findViewById(R.id.nav_view);

        MaterialCardView timetableCard = view.findViewById(R.id.timetable_card);
        MaterialCardView illnessDocCard = view.findViewById(R.id.illness_doc_card);
        MaterialCardView foodPlanCard = view.findViewById(R.id.food_plan_card);
        MaterialCardView substitutionCard = view.findViewById(R.id.subs_card);
        MaterialCardView newsCard = view.findViewById(R.id.news_card);
        MaterialCardView customCard = view.findViewById(R.id.custom_card);

        timetableCard.setOnClickListener(v -> startActivity(new Intent(getContext(), TimetableActivity.class)));
        illnessDocCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ApiClient.getInstance().getData().getContentByKey("DATA_ILLNESS_DOC_" + (ClassUtils.isAdvancedClass(sClass) ? 1 : 0)).getValue()))));
        foodPlanCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ApiClient.getInstance().getData().getContentByKey("DATA_FOOD_PLAN").getValue()))));
        substitutionCard.setOnClickListener(v -> navController.navigate(R.id.navigation_substitutions));
        if (ApiClient.getInstance().getData().getContentByKey("DATA_CUSTOM") != null) {
            customCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ApiClient.getInstance().getData().getContentByKey("DATA_CUSTOM").getValue()))));
        } else {
            customCard.setVisibility(View.GONE);
        }

        newsCard.setOnClickListener(v -> showNewsFragment());

        return view;
    }

    private void showNewsFragment() {
        // workaround to uncheck the currently selected item
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        navView.getMenu().getItem(0).setCheckable(false);

        // navigate to news fragment
        navController.navigate(R.id.navigation_news);
    }
}