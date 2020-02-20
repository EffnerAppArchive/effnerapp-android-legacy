package de.effnerapp.effner.data.model.color;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ColorParser {
    private Gson gson;

    public ColorParser() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public RGBAColor parseRGBAColor(String json) {
        return gson.fromJson(json, RGBAColor.class);
    }
}
