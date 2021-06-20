package de.effnerapp.effner.ui.fragments.mvv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.mvv.MvvClient;
import de.effnerapp.effner.data.mvv.json.Departure;
import de.effnerapp.effner.data.mvv.json.StopItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MVVFragment extends Fragment {

    private final MvvClient mvvClient;

    private List<Departure> departures;
    private DepartureItemAdapter departureItemAdapter;


    public MVVFragment() {
        // Required empty public constructor
        mvvClient = new MvvClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mvv, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.mvv_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        departures = new ArrayList<>();

        departureItemAdapter = new DepartureItemAdapter(departures, requireActivity());
        recyclerView.setAdapter(departureItemAdapter);


        AutoCompleteTextView stopInput = view.findViewById(R.id.input_stop);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(requireContext(), android.R.layout.select_dialog_item, mvvClient);

        stopInput.setThreshold(4);
        stopInput.setAdapter(adapter);

        stopInput.setOnItemClickListener((parent, v, position, id) -> {
            hideKeyboardFrom(requireContext(), view.findViewById(R.id.container));
            stopInput.clearFocus();

            StopItem stopItem = (StopItem) parent.getItemAtPosition(position);
            fetchDepartures(stopItem.getId());
            sharedPreferences.edit()
                    .putString("APP_MVV_LAST_STOP_ID", stopItem.getId())
                    .putString("APP_MVV_LAST_STOP_NAME", stopItem.getName())
                    .apply();
        });

        String lastStopId = sharedPreferences.getString("APP_MVV_LAST_STOP_ID", "de:09174:6800");
        String lastStopName = sharedPreferences.getString("APP_MVV_LAST_STOP_NAME", "Dachau");

        stopInput.setText(lastStopName);
        fetchDepartures(lastStopId);


        // update time every minute
        Timer timer = new Timer();
        Calendar calendar = Calendar.getInstance();
        int sec = calendar.get(Calendar.SECOND);
        int delta = 60 - sec;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isVisible()) {
                    Log.d("MVVFrag", "Fragment detached! Stopping timer ...");
                    timer.cancel();
                    return;
                }

                requireActivity().runOnUiThread(() -> Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged());
            }
        }, delta * 1000, 60 * 1000);

        return view;
    }

    private void fetchDepartures(String stopId) {
        mvvClient.loadDepartures(stopId, data -> {
            if (isVisible()) {
                departures.clear();
                departures.addAll(Arrays.asList(data.getDepartures()));
                requireActivity().runOnUiThread(() -> departureItemAdapter.notifyDataSetChanged());
            }
        });
    }

    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}