package de.effnerapp.effner.ui.substitutions.sections;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Head extends ExpandableGroup<Item> {
    private int color;
    public Head(String title, List<Item> items, int color) {
        super(title, items);
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
