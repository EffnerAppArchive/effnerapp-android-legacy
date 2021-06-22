/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.05.21, 18:07.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.dsbmobile.model;

import java.util.ArrayList;
import java.util.List;

public class Day {

    private final List<SClass> sClasses = new ArrayList<>();
    private final String date;

    public Day(String date) {
        this.date = date;
    }

    public List<SClass> getSClasses() {
        return sClasses;
    }

    public void addSClasses(SClass sClass) {
        sClasses.add(sClass);
    }

    public String getDate() {
        return date;
    }
}
