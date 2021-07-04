/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:20.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.information;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.ApiClient;
import de.effnerapp.effner.data.api.json.data.Document;
import de.effnerapp.effner.ui.activities.splash.SplashActivity;

public class InformationFragment extends Fragment {

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.information_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Document> items = new ArrayList<>();

        if(ApiClient.getInstance() == null) {
            startActivity(new Intent(requireContext(), SplashActivity.class));
            requireActivity().finish();
            return view;
        }

        for (Document document : ApiClient.getInstance().getData().getDocuments()) {
            if (document.getKey().startsWith("DATA_INFORMATION")) {
                items.add(document);
            }
        }

        InformationItemAdapter adapter = new InformationItemAdapter(items);

        recyclerView.setAdapter(adapter);
        return view;
    }
}