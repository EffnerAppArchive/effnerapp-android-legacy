/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:20.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.mvv;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.mvv.json.Departure;

public class DepartureItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Departure> departures;
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
    private final SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.GERMAN);
    private final Activity activity;

    DepartureItemAdapter(List<Departure> departures, Activity activity) {
        this.departures = departures;
        this.activity = activity;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // create a new view
        if (viewType == 0) {
            return new DepartureItemAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.departure_item, parent, false));
        } else {
            return new DepartureItemAdapter.InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.no_departures_item, parent, false));
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder.getItemViewType() == 0) {
            DepartureItemAdapter.ItemViewHolder iHolder = (DepartureItemAdapter.ItemViewHolder) holder;
            Departure departure = departures.get(i);

            String departureDate = departure.getDepartureDate();
            String departurePlanned = departure.getDeparturePlanned();
            String departureLive = departure.getDepartureLive();

            try {
                int dP = Integer.parseInt(departurePlanned.replace(":", ""));
                int dL = Integer.parseInt(departureLive.replace(":", ""));

                if (dP >= dL) {
                    iHolder.time.setTextColor(activity.getResources().getColor(R.color.green));
                } else {
                    iHolder.time.setTextColor(activity.getResources().getColor(R.color.red));
                }
            } catch (NumberFormatException e) {
                iHolder.time.setTextColor(activity.getResources().getColor(R.color.red));
            }

            if (departure.getLine().getNumber().startsWith("S")) {
                iHolder.line.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.mvv_badge_train)));
            } else if(departure.getLine().getNumber().startsWith("RB")) {
                iHolder.line.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.mvv_badge_regio)));
            } else {
                iHolder.line.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.mvv_badge_bus)));
            }
            iHolder.line.setText(departure.getLine().getNumber());

            // very ugly
            String[] t = departure.getDirection().split(" ");
            String dT = t.length >= 2 ? t[0] + " " + t[1] : t[0];
            iHolder.destination.setText(dT);

            try {
                Date departureTime = sourceFormat.parse(departureDate + " " + departureLive);

                assert departureTime != null;
                long diff = departureTime.getTime() - System.currentTimeMillis();

                iHolder.time.setText(getDepartureTime(diff));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getItemCount() {
        if(departures.isEmpty()) {
            return 1;
        }
        return departures.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!departures.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }


    private String getDepartureTime(long millis) {
        long hours = Math.max(0, TimeUnit.MILLISECONDS.toHours(millis));
        long minutes = Math.max(0, TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours));

        return hours == 0 && minutes == 0 ? "jetzt" : "in " + String.format(Locale.GERMAN, "%02d:%02d", hours, minutes);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView line;
        final TextView destination;
        final TextView time;

        ItemViewHolder(@NonNull View view) {
            super(view);
            line = view.findViewById(R.id.departure_line);
            destination = view.findViewById(R.id.departure_destination);
            time = view.findViewById(R.id.departure_time);
        }
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {

        public InfoViewHolder(@NonNull View view) {
            super(view);
        }
    }
}