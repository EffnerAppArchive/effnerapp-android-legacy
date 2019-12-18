package de.effnerapp.effner.data.dsbmobile.model;

import java.util.ArrayList;
import java.util.List;

public class Tag {

    private List<Klasse> klassen = new ArrayList<>();
    private String datum;

    public Tag(String datum) {
        this.datum = datum;
    }

    public List<Klasse> getKlassen() {
        return klassen;
    }

    public void addKlassen(Klasse klasse) {
        klassen.add(klasse);
    }

    public String getDatum() {
        return datum;
    }
}
