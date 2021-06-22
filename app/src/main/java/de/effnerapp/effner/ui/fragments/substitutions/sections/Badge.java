/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 22.06.21, 19:43.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.substitutions.sections;

import android.graphics.Color;

public class Badge {
    private final int id;
    private final String text;
    private final int color;

    public Badge(int id, String text) {
        this(id, text, Color.WHITE);
    }

    public Badge(int id, String text, int color) {
        this.id = id;
        this.text = text;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getColor() {
        return color;
    }
}
