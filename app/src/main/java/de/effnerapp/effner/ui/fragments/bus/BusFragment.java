package de.effnerapp.effner.ui.fragments.bus;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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
                DepartureItemAdapter adapter = new DepartureItemAdapter(new ArrayList<>(Arrays.asList(data.getDepartures())), requireActivity());
                requireActivity().runOnUiThread(() -> recyclerView.setAdapter(adapter));
            }
        });

        // update time every minute
        Timer timer = new Timer();
        Calendar calendar = Calendar.getInstance();
        int sec = calendar.get(Calendar.SECOND);
        int delta = 60 - sec;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isVisible()) {
                    Log.d("BusFrag", "Fragment detached! Stopping timer ...");
                    timer.cancel();
                    return;
                }

                requireActivity().runOnUiThread(() -> Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged());
            }
        }, delta * 1000, 60 * 1000);

        return view;
    }
}