/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.05.21, 18:07.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.news.sections;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Head extends ExpandableGroup<Item> {
    private final int color;
    private final List<String> documents;

    public Head(String title, List<Item> items, List<String> documents, int color) {
        super(title, items);
        this.documents = documents;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public List<String> getDocuments() {
        return documents;
    }
}