package de.effnerapp.effner.ui.substitutions.sections;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Head extends ExpandableGroup<Item> {

    public Head(String title, List<Item> items) {
        super(title, items);
    }


}
