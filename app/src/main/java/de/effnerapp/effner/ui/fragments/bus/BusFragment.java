package de.effnerapp.effner.ui.fragments.bus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.mvv.MvvClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusFragment extends Fragment {

    public BusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.bus_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        MvvClient mvvClient = new MvvClient();

        mvvClient.loadData((isSuccess, data) -> {
            if (isSuccess) {
                DepartureItemAdapter adapter = new DepartureItemAdapter(Arrays.asList(data.getDepartures()));
                requireActivity().runOnUiThread(() -> recyclerView.setAdapter(adapter));
            }
        });

        return view;
    }
}