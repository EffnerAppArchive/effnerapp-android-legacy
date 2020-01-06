package de.effnerapp.effner.data.dsbmobile;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.effnerapp.effner.data.dsbmobile.model.Klasse;
import de.effnerapp.effner.data.dsbmobile.model.Tag;
import de.effnerapp.effner.data.dsbmobile.model.Vertretung;
import de.sematre.dsbmobile.DSBMobile;

public class Vertretungen {

    private DSBMobile dsbMobile;
    private String url;
    private List<Tag> table;
    private List<String> tage = new ArrayList<>();
    private List<List<String>> mainInformation = new ArrayList<>();


    public Vertretungen() {
        //Required!!! xD
    }

    public boolean login(String username, String password) {
        try {
            new Thread(() -> dsbMobile = new DSBMobile(username, password)).start();
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void load() throws InterruptedException {
        while (dsbMobile == null) {
            Thread.sleep(500);
        }
        new Thread(() -> {
            ArrayList<DSBMobile.TimeTable> timeTables = dsbMobile.getTimeTables();
            url = timeTables.get(0).getDetail();
            List<Tag> days = new ArrayList<>();
            Document document = null;
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] tableN = new String[10];
            int tableI = 0;

            assert document != null;
            for (Element a : document.select("a")) {
                if (a.text().equals("") || a.text() == null) continue;
                Log.d("Vertretungen", "Days: " + a.text());
                tage.add(tableI, a.text());
                tableI++;
                tableN[tableI] = a.text();
            }

            tableI = 0;

            mainInformation.clear();
            int i = 0;
            for (Element table : document.select("table")) {
                if(table.attr("class").equals("F")) {
                    if(i == 0 || i == 2) {
                        List<String> items = new ArrayList<>();
                        for(Element th : table.select("th")) {
                            items.add(th.text());
                        }
                        mainInformation.add(items);
                    }
                    i++;
                }

                if (table.text().contains("vertreten durch")) {
                    tableI++;
                    Tag tag = new Tag(tableN[tableI]);

                    for (Element body : table.select("tbody")) {
                        if (body.select("th").text().startsWith("Klasse")) continue;
                        Klasse klasse = new Klasse(body.select("th").text());

                        for (Element row : body.select("tr")) {
                            Vertretung vertretung = new Vertretung();

                            int val = 0;
                            for (Element info : row.select("td")) {
                                val++;
                                switch (val) {
                                    case 1:
                                        vertretung.setLehrkraft(info.text());
                                        break;
                                    case 2:
                                        vertretung.setStunde(info.text());
                                        break;
                                    case 3:
                                        vertretung.setVertretung(info.text());
                                        break;
                                    case 4:
                                        vertretung.setRaum(info.text());
                                        break;
                                    case 5:
                                        vertretung.setInfo(info.text());
                                        break;
                                }
                            }

                            klasse.addVertretungen(vertretung);

                        }

                        tag.addKlassen(klasse);

                    }

                    days.add(tag);

                }
            }

            table = days;
        }).start();
    }


    public List<Tag> getTable() {
        return table;
    }

    public List<String> getTage() {
        return tage;
    }

    public List<List<String>> getMainInformation() {
        return mainInformation;
    }

}