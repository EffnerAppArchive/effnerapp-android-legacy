package de.effnerapp.effner.data.dsbmobile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.effnerapp.effner.data.dsbmobile.model.Day;
import de.effnerapp.effner.data.dsbmobile.model.SClass;
import de.effnerapp.effner.data.dsbmobile.model.Substitution;
import de.sematre.dsbmobile.DSBMobile;

public class Substitutions {
    private String username;
    private String password;
    private DSBMobile dsbMobile;
    private String url;
    private List<String> dates;
    private List<Day> days;
    private Map<String, String> information;

    public Substitutions(String username, String password) {
        this.username = username;
        this.password = password;
        dates = new ArrayList<>();
        days = new ArrayList<>();
        information = new HashMap<>();
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
            url = timeTable.getDetail();
            break;
        }

        dates.clear();
        days.clear();
        information.clear();
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
                if(table.attr("class").equals("F")) {
                    StringBuilder sb = new StringBuilder();
                    for(Element th : table.select("th")) {
                        sb.append(th.text()).append("\n");
                    }
                    information.put(date, sb.toString());
                } else if(table.attr("class").equals("K")) {
                    //TODO: handle absent classes!

                } else if (table.attr("class").equals("k")) {
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
}
