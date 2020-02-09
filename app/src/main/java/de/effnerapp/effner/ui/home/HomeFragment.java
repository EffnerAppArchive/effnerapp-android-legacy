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

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.util.Objects;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.TimetableActivity;
import de.effnerapp.effner.data.DataStack;
import de.effnerapp.effner.data.utils.HeaderTextParser;

public class HomeFragment extends Fragment {
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity.pageTextView.setText(R.string.title_home);
        TextView headerTextView = root.findViewById(R.id.headertextview);
        DataStack dataStack = SplashActivity.getDataStack();
        String headerText = "Noch 42 Tage bis zum Wochenende!";
        try {
            headerText = new HeaderTextParser().parse(dataStack.getHolidays(), SplashActivity.sharedPreferences.getString("APP_USER_USERNAME", ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        headerTextView.setText(headerText);

        String sClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");

        CardView sCard = root.findViewById(R.id.stundenplan_card);
        CardView iCard = root.findViewById(R.id.illness_doc_card);
        CardView cCard = root.findViewById(R.id.campuscafe_card);
        CardView subCard = root.findViewById(R.id.subs_card);

        sCard.setOnClickListener(view -> startActivity(new Intent(getContext(), TimetableActivity.class)));
        iCard.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SplashActivity.getDataStack().getContentByKey("DATA_ILLNESS_DOC_" +  ((sClass.startsWith("11") || sClass.startsWith("12")) ? 1 : 0)).getValue()))));
        cCard.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SplashActivity.getDataStack().getContentByKey("DATA_FOOD_PLAN").getValue()))));
        subCard.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_substitutions);
        });
        return root;
    }
}