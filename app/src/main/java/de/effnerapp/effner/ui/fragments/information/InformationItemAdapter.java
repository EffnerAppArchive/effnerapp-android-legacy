/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.fragments.information;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.json.data.Document;
import de.effnerapp.effner.tools.view.IntentHelper;
import de.effnerapp.effner.ui.activities.main.MainActivity;

public class InformationItemAdapter extends RecyclerView.Adapter<InformationItemAdapter.ItemViewHolder> {
    private final List<Document> items;

    InformationItemAdapter(List<Document> items) {
        this.items = items;
    }

    @NotNull
    @Override
    public InformationItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        return new InformationItemAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull InformationItemAdapter.ItemViewHolder holder, int i) {
        String text = items.get(i).getName();
        holder.itemText.setText(text.replace("DATA_INFORMATION_", ""));
        holder.cardView.setOnClickListener(v -> IntentHelper.openView(MainActivity.getInstance(), items.get(i).getUri()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView cardView;
        final TextView itemText;
        final TextView badge0;
        final TextView badge1;

        ItemViewHolder(@NonNull View view) {
            super(view);
            cardView = view.findViewById(R.id.item_card);
            itemText = view.findViewById(R.id.information_text_view);
            badge0 = view.findViewById(R.id.information_badge_0);
            badge1 = view.findViewById(R.id.information_badge_1);
        }
    }
}
