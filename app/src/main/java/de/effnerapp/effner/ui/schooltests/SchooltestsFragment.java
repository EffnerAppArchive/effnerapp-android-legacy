package de.effnerapp.effner.ui.schooltests;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Schooltest> schooltests = new ArrayList<>(Arrays.asList(SplashActivity.getDataStack().getSchooltests()));
        adapter = new SchooltestItemAdapter(schooltests);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
