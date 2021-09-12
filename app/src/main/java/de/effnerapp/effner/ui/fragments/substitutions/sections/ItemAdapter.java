/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.fragments.substitutions.sections;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import de.effnerapp.effner.R;

public class ItemAdapter extends ExpandableRecyclerViewAdapter<HeadViewHandler, ItemViewHandler> {


    public ItemAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public HeadViewHandler onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_substitution_head, parent, false);
        return new HeadViewHandler(view);
    }

    @Override
    public ItemViewHandler onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_substitution_body, parent, false);
        return new ItemViewHandler(view);
    }

    @Override
    public void onBindChildViewHolder(ItemViewHandler holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Item item = (Item) group.getItems().get(childIndex);
        holder.bind(item);
    }

    @Override
    public void onBindGroupViewHolder(HeadViewHandler holder, int flatPosition, ExpandableGroup group) {
        final Head head = (Head) group;
        holder.bind(head);
    }
}
