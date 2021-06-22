/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.05.21, 18:07.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.substitutions.sections;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

public class Head extends ExpandableGroup<Item> {
    private final int color;
    private final List<Badge> badges;

    public Head(String title, List<Item> items, int color) {
        this(title, items, color, new ArrayList<>());
    }

    public Head(String title, List<Item> items, int color, List<Badge> badges) {
        super(title, items);
        this.color = color;
        this.badges = badges;
    }

    public void addBadge(Badge badge) {
        badges.add(badge);
    }

    public int getColor() {
        return color;
    }

    public List<Badge> getBadges() {
        return badges;
    }
}
