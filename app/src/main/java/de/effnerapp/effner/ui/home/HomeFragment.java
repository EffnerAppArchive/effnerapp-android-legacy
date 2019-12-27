package de.effnerapp.effner.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;

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

            headerText = new HeaderTextParser().parse(dataStack.getHolidays());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        headerTextView.setText(headerText);

        CardView sCard = root.findViewById(R.id.stundenplan_card);
        CardView hCard = root.findViewById(R.id.hausaufgaben_card);
        sCard.setOnClickListener(view -> startActivity(new Intent(getContext(), TimetableActivity.class)));
        hCard.setOnClickListener(view -> {
            Snackbar snackbar = Snackbar.make(view,"Hier wird noch gearbeitet!",Snackbar.LENGTH_SHORT);
            snackbar.setAction("Schließen", v -> {

            }) ;
            snackbar.show();
        });
        
        return root;
    }
}