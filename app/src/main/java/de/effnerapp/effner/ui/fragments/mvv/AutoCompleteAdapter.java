/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:20.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.mvv;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.effnerapp.effner.data.mvv.MvvClient;
import de.effnerapp.effner.data.mvv.json.FindStopResponse;
import de.effnerapp.effner.data.mvv.json.StopItem;

public class AutoCompleteAdapter extends ArrayAdapter<StopItem> implements Filterable {
    private List<StopItem> data;
    private final MvvClient mvvClient;

    public AutoCompleteAdapter(@NonNull Context context, int textViewResourceId, MvvClient mvvClient) {
        super(context, textViewResourceId);
        this.mvvClient = mvvClient;
        data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public StopItem getItem(int i) {
        return data.get(i);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if(constraint != null) {
                    FindStopResponse filteredData = mvvClient.findStop(constraint.toString());

                    data = Arrays.asList(filteredData.getResults());

                    filterResults.values = data;
                    filterResults.count = data.size();

                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
