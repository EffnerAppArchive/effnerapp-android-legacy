package de.effnerapp.effner.data.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.List;

import de.effnerapp.effner.data.model.News;
import de.effnerapp.effner.ui.news.NewsItem;

public class NewsParser {

    public NewsParser() {

    }

    public List<NewsItem> parse(News[] news) {
        List<NewsItem> newsItems = new ArrayList<>();

        for(News jNews : news) {
            NewsItem newsItem = new NewsItem();
            newsItem.setDate(jNews.getDate());
            newsItem.setUrls(parseUrls(jNews));

            if(!jNews.getTitle().isEmpty()) {
                Document titleDoc = Jsoup.parse(jNews.getTitle());
                titleDoc.outputSettings(new Document.OutputSettings().prettyPrint(false));
                for(Element br : titleDoc.select("br")) {
                    br.after(new TextNode("#NEW_LINE# "));
                }
                String title = Jsoup.clean(titleDoc.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false))
                        .replace("&nbsp;", "")
                        .replaceAll("\\s+", " ")
                        .replace("#NEW_LINE# ", "\n\n").trim();
                newsItem.setTitle(title);
            }

            if(!jNews.getContent().isEmpty()) {
                Document contentDoc = Jsoup.parse(jNews.getContent());
                contentDoc.outputSettings(new Document.OutputSettings().prettyPrint(false));
                for(Element br : contentDoc.select("br")) {
                    br.after(new TextNode("#NEW_LINE# "));
                }
                String content = Jsoup.clean(contentDoc.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false))
                        .replace("&nbsp;", "")
                        .replaceAll("\\s+", " ")
                        .replace("#NEW_LINE# ", "\n\n").trim();
                newsItem.setContent(content);
            }

            newsItems.add(newsItem);
        }

        return newsItems;
    }


    private List<String> parseUrls(News jNews) {
        List<String> urls = new ArrayList<>();
        for(String data : jNews.getData()) {
            Document document = Jsoup.parse(data);
            for(Element a : document.select("a")) {
                if(isUrlAllowed(a.attr("href"))) {
                    String url = buildURL(a.attr("href"));
                    if(!urls.contains(url)) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    private String buildURL(String url) {
        if(url.startsWith("mailto:")) {
            return url;
        } else {
            return (url.startsWith("http://") || url.startsWith("https://") ? url : "https://effner.de" + url);
        }
    }

    private boolean isUrlAllowed(String url) {
        return url.startsWith("https://") || url.startsWith("http://") || url.startsWith("mailto:") || url.startsWith("/");
    }
}
