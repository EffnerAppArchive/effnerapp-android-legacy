package de.effnerapp.effner.data.dsbmobile;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.effnerapp.effner.data.dsbmobile.model.AbsentClass;
import de.effnerapp.effner.data.dsbmobile.model.Day;
import de.effnerapp.effner.data.dsbmobile.model.SClass;
import de.effnerapp.effner.data.dsbmobile.model.Substitution;
import de.sematre.dsbmobile.DSBMobile;

public class Substitutions {
    private final String username;
    private final String password;
    private DSBMobile dsbMobile;
    private String url;
    private final List<String> dates;
    private final List<Day> days;
    private final Map<String, String> information;
    private final List<AbsentClass> absentClasses;

    public Substitutions(String username, String password) {
        this.username = username;
        this.password = password;
        dates = new ArrayList<>();
        days = new ArrayList<>();
        information = new HashMap<>();
        absentClasses = new ArrayList<>();
    }


    public boolean login() {
        try {
            dsbMobile = new DSBMobile(username, password);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public void load() {
        if(dsbMobile == null) {
            login();
        }
        for (DSBMobile.TimeTable timeTable : dsbMobile.getTimeTables()) {
            url = timeTable.getDetail(); // TimeTable#getDetail() contains url.
            break;
        }

        dates.clear();
        days.clear();
        information.clear();
        absentClasses.clear();

        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert document != null;
        for (Document doc : splitDocument(document)) {
            String date = null;
            for(Element a : doc.select("a")) {
                if(!a.attr("name").isEmpty()) {
                    date = a.attr("name");
                    dates.add(date);
                }
            }
            for (Element table : doc.select("table")) {
                switch (table.attr("class")) {
                    case "F":
                        StringBuilder sb = new StringBuilder();
                        for (Element th : table.select("th")) {
                            sb.append(th.text()).append("\n");
                        }
                        if (!sb.toString().isEmpty()) {
                            Log.d("Subs", "Adding general information for date " + date);
                            information.put(date, sb.toString());
                        }

                        break;
                    case "K":
                        for (Element tr : table.select("tr")) {
                            String sClass = "?", period = "?";
                            for (Element th : tr.select("th")) {
                                sClass = th.text();
                            }

                            for (Element td : tr.select("td")) {
                                period = td.text();
                            }

                            absentClasses.add(new AbsentClass(date, sClass, period));
                        }

                        break;
                    case "k":
                        Day day = new Day(date);
                        for (Element body : table.select("tbody")) {
                            if (body.select("th").text().startsWith("Klasse")) continue;
                            SClass sClass = new SClass(body.select("th").text());

                            for (Element row : body.select("tr")) {
                                Substitution substitution = new Substitution();

                                int i = 0;
                                for (Element column : row.select("td")) {
                                    i++;
                                    switch (i) {
                                        case 1:
                                            substitution.setTeacher(column.text());
                                            break;
                                        case 2:
                                            substitution.setPeriod(column.text());
                                            break;
                                        case 3:
                                            substitution.setSubTeacher(column.text());
                                            break;
                                        case 4:
                                            substitution.setRoom(column.text());
                                            break;
                                        case 5:
                                            substitution.setInfo(column.text());
                                            break;
                                    }
                                }
                                sClass.addSubstitutions(substitution);
                            }
                            day.addSClasses(sClass);
                        }
                        days.add(day);
                        break;
                }
            }
        }
    }

    private List<Document> splitDocument(Document document) {

        List<String> elements = new ArrayList<>();
        for(Element a : document.select("a")) {
            if(!a.attr("name").isEmpty()) {
                elements.add("<a name=\"" + a.attr("name") + "\">");
            }
        }

        List<Document> splitDocuments = new ArrayList<>();
        String val = document.toString();
        for (int i = 0; i < elements.size(); i++) {
            if(i == 0) {
                splitDocuments.add(Jsoup.parse(val.substring(0, val.indexOf(elements.get(i)))));
            } else if(i == elements.size() - 1) {
                splitDocuments.add(Jsoup.parse(val.substring(val.indexOf(elements.get(i)))));
            } else {
                splitDocuments.add(Jsoup.parse(val.substring(val.indexOf(elements.get(i)), val.indexOf(elements.get(i + 1)))));
            }
        }
        return splitDocuments;
    }

    public List<Day> getDays() {
        return days;
    }

    public List<String> getDates() {
        return dates;
    }

    public Map<String, String> getInformation() {
        return information;
    }

    public List<AbsentClass> getAbsentClasses() {
        return absentClasses;
    }
}
