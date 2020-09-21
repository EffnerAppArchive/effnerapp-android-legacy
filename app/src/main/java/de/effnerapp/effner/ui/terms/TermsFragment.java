package de.effnerapp.effner.ui.terms;

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

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.data.model.Term;


public class TermsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TermItemAdapter adapter;

    public TermsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_terms, container, false);
        recyclerView = view.findViewById(R.id.terms_recycler_view);
        Spinner spinner = view.findViewById(R.id.spinner);
        String[] items = {"Neuste zuerst", "Älteste zuerst"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Term> terms = new ArrayList<>(Arrays.asList(SplashActivity.getData().getTerms()));
        adapter = new TermItemAdapter(terms);
        recyclerView.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TermsSpinner", "Item: " + parent.getItemAtPosition(position));
                if (position == 1) {
                    List<Term> list = new ArrayList<>(terms);
                    Collections.reverse(list);
                    adapter = new TermItemAdapter(list);
                } else {
                    adapter = new TermItemAdapter(terms);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.w("TermsSpinner", "Nothing selected!");
            }
        });
        return view;
    }

}
