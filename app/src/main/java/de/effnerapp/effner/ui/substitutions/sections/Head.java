package de.effnerapp.effner.ui.substitutions.sections;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

public class Head extends ExpandableGroup<Item> {
    private int color;
    private List<Badge> badges;

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
