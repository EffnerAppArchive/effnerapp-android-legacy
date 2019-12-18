package de.effnerapp.effner.data.dsbmobile.model;

public class Vertretung {

    private String Lehrkraft, Stunde, Vertretung, Raum, Info;

    public Vertretung() {

    }

    public void setLehrkraft(String lehrkraft) {
        Lehrkraft = lehrkraft;
    }

    public void setStunde(String stunde) {
        Stunde = stunde;
    }

    public void setVertretung(String vertretung) {
        Vertretung = vertretung;
    }

    public void setRaum(String raum) {
        Raum = raum;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public String getLehrkraft() {
        return Lehrkraft;
    }

    public String getStunde() {
        return Stunde;
    }

    public String getVertretung() {
        return Vertretung;
    }

    public String getRaum() {
        return Raum;
    }

    public String getInfo() {
        return Info;
    }
}


