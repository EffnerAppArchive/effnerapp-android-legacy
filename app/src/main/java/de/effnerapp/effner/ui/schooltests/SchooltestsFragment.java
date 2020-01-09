package de.effnerapp.effner.ui.schooltests;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.data.model.Schooltest;

public class SchooltestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SchooltestItemAdapter adapter;

    public SchooltestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schooltests, container, false);
        MainActivity.pageTextView.setText(R.string.title_schooltests);
        recyclerView = view.findViewById(R.id.recyclerview);
        Spinner spinner = view.findViewById(R.id.spinner);
        String[] items = {"Neuste zuerst", "Älteste zuerst"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Schooltest> schooltests = new ArrayList<>(Arrays.asList(SplashActivity.getDataStack().getSchooltests()));
        adapter = new SchooltestItemAdapter(schooltests);
        recyclerView.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SchooltestSpinner", "Item: " + parent.getItemAtPosition(position));
                if(position == 1) {
                    List<Schooltest> list = new ArrayList<>(schooltests);
                    Collections.reverse(list);
                    adapter = new SchooltestItemAdapter(list);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter = new SchooltestItemAdapter(schooltests);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.w("SchooltestSpinner","Nothing selected!");
            }
        });
        return view;
    }

}
