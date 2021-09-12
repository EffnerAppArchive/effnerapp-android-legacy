/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.data.api.json.data.color;

import android.graphics.Color;

public class RGBAColor {
    private int r, g, b, a;

    public int getColorValue() {
        return Color.argb(a, r, g, b);
    }
}
