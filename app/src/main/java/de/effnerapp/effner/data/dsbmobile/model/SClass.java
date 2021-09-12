/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
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
