/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.05.21, 18:07.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.data.dsbmobile.model;

import java.util.ArrayList;
import java.util.List;

public class SClass {

    private final String name;
    private final List<Substitution> substitutions = new ArrayList<>();

    public SClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSubstitutions(Substitution substitution) {
        substitutions.add(substitution);
    }

    public List<Substitution> getSubstitutions() {
        return substitutions;
    }
}
