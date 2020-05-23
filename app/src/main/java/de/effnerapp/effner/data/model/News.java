package de.effnerapp.effner.data.model;

public class News {
    private String title, content, date;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String[] getData() {
        return new String[] {title, content};
    }

    public String getDate() {
        return date;
    }
}

