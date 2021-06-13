package de.effnerapp.effner.data.mvv;

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
