package de.effnerapp.effner.data.model;

public class News {
    private Rendered title, content;
    private String date;

    public Rendered getTitle() {
        return title;
    }

    public Rendered getContent() {
        return content;
    }

    public Rendered[] getRendered() {
        return new Rendered[] {title, content};
    }

    public String getDate() {
        return date;
    }

    public static class Rendered {
        private String rendered;

        public String getValue() {
            return rendered;
        }
    }
}

