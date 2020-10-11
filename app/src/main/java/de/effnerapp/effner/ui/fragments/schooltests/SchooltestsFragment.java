package de.effnerapp.effner.ui.fragments.schooltests;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.model.Content;
import de.effnerapp.effner.data.model.Schooltest;
import de.effnerapp.effner.data.utils.ApiClient;
import de.effnerapp.effner.tools.ClassUtils;

public class SchooltestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SchooltestItemAdapter adapter;

    public SchooltestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schooltests, container, false);
        recyclerView = view.findViewById(R.id.schooltests_recycler_view);
        Spinner spinner = view.findViewById(R.id.spinner);

        String sClass = sharedPreferences.getString("APP_USER_CLASS", "");

        if (!ClassUtils.isAdvancedClass(sClass)) {
            String[] items = {"Neuste zuerst", "Älteste zuerst"};
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            List<Schooltest> schooltests = new ArrayList<>(Arrays.asList(ApiClient.getInstance().getData().getSchooltests()));
            adapter = new SchooltestItemAdapter(schooltests);
            recyclerView.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("SchooltestSpinner", "Item: " + parent.getItemAtPosition(position));
                    if (position == 1) {
                        List<Schooltest> list = new ArrayList<>(schooltests);
                        Collections.reverse(list);
                        adapter = new SchooltestItemAdapter(list);
                    } else {
                        adapter = new SchooltestItemAdapter(schooltests);
                    }
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.w("SchooltestSpinner", "Nothing selected!");
                }
            });

        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            GridLayout gridLayout = view.findViewById(R.id.schooltests_card_layout);
            gridLayout.setVisibility(View.VISIBLE);

            CardView h1Card = view.findViewById(R.id.h1_card);
            CardView h2Card = view.findViewById(R.id.h2_card);
            String key = "DATA_TOP_LEVEL_SA_DOC_" + ClassUtils.getFirstDigits(sClass) + "_";

            h1Card.setOnClickListener(v -> {
                Content content = ApiClient.getInstance().getData().getContentByKey(key + 1);
                if (content != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(content.getValue())));
                } else {
                    Toast.makeText(getContext(), "Dieses Dokument ist nicht verfügbar!", Toast.LENGTH_SHORT).show();
                }
            });

            h2Card.setOnClickListener(v -> {
                Content content = ApiClient.getInstance().getData().getContentByKey(key + 2);
                if (content != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(content.getValue())));
                } else {
                    Toast.makeText(getContext(), "Dieses Dokument ist nicht verfügbar!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

}
