package de.effnerapp.effner.data.dsbmobile.model;

import java.util.ArrayList;
import java.util.List;

public class Klasse {

    private String name;
    private List<Vertretung> vertretungen = new ArrayList<>();

    public Klasse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addVertretungen(Vertretung vertretung) {
        vertretungen.add(vertretung);
    }

    public List<Vertretung> getVertretungen() {
        return vertretungen;
    }
}
