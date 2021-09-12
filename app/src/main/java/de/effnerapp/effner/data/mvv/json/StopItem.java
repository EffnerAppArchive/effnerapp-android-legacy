/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.data.mvv.json;

import org.jetbrains.annotations.NotNull;

public class StopItem {
    private String name, id, anyType, stateless;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAnyType() {
        return anyType;
    }

    public String getStateless() {
        return stateless;
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }
}
