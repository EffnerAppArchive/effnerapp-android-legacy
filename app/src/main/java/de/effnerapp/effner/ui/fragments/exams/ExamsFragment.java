/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.fragments.exams;


import android.content.SharedPreferences;
import android.os.Bundle;
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
import de.effnerapp.effner.data.api.ApiClient;
import de.effnerapp.effner.data.api.json.data.Document;
import de.effnerapp.effner.data.api.json.data.Exam;
import de.effnerapp.effner.tools.misc.ClassUtils;
import de.effnerapp.effner.tools.view.IntentHelper;

public class ExamsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ExamItemAdapter adapter;

    public ExamsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exams, container, false);
        recyclerView = view.findViewById(R.id.exams_view);
        Spinner spinner = view.findViewById(R.id.spinner);

        String sClass = sharedPreferences.getString("APP_USER_CLASS", "");

        if (!ClassUtils.isAdvancedClass(sClass)) {
            String[] items = getResources().getStringArray(R.array.spin_sort_items);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            List<Exam> exams = new ArrayList<>(Arrays.asList(ApiClient.getInstance().getData().getExams().getExams()));
            Collections.reverse(exams);
            adapter = new ExamItemAdapter(requireContext(), exams);
            recyclerView.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 1) {
                        List<Exam> list = new ArrayList<>(exams);
                        Collections.reverse(list);
                        adapter = new ExamItemAdapter(requireContext(), list);
                    } else {
                        adapter = new ExamItemAdapter(requireContext(), exams);
                    }
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

        } else {
            recyclerView.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            GridLayout gridLayout = view.findViewById(R.id.exams_card_layout);
            gridLayout.setVisibility(View.VISIBLE);
            View disclaimerInfo = view.findViewById(R.id.disclaimer_info);
            disclaimerInfo.setVisibility(View.VISIBLE);

            CardView h1Card = view.findViewById(R.id.h1_card);
            CardView h2Card = view.findViewById(R.id.h2_card);
            String keyPrefix = "DATA_TOP_LEVEL_SA_DOC_" + ClassUtils.getFirstDigits(sClass) + "_";

            h1Card.setOnClickListener(v -> {
                Document document = ApiClient.getInstance().getData().getDocumentByKey(keyPrefix + "1");
                if (document != null) {
                    IntentHelper.openView(requireContext(), document.getUri());
                } else {
                    Toast.makeText(getContext(), R.string.t_document_unavailable, Toast.LENGTH_SHORT).show();
                }
            });

            h2Card.setOnClickListener(v -> {
                Document document = ApiClient.getInstance().getData().getDocumentByKey(keyPrefix + "2");
                if (document != null) {
                    IntentHelper.openView(requireContext(), document.getUri());
                } else {
                    Toast.makeText(getContext(), R.string.t_document_unavailable, Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

}
