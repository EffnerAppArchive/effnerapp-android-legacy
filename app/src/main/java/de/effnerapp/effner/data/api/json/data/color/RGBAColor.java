package de.effnerapp.effner.data.api.json.data.color;

import android.graphics.Color;

public class RGBAColor {
    private int r, g, b, a;

    public int getColorValue() {
        return Color.argb(a, r, g, b);
    }
}
