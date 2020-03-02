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
            newsItem.setUrls(parseUrls(jNews));

            if(!jNews.getTitle().getValue().isEmpty()) {
                Document titleDoc = Jsoup.parse(jNews.getTitle().getValue());
                titleDoc.outputSettings(new Document.OutputSettings().prettyPrint(false));
                for(Element br : titleDoc.select("br")) {
                    br.after(new TextNode("\n"));
                }
                String title = Jsoup.clean(titleDoc.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false)).replace("&#8230;weiterlesen", "");
                newsItem.setTitle(title);
            }

            if(!jNews.getContent().getValue().isEmpty()) {
                Document contentDoc = Jsoup.parse(jNews.getContent().getValue());
                for(Element br : contentDoc.select("br")) {
                    br.after(new TextNode("\n"));
                }
                String content = Jsoup.clean(contentDoc.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false)).replace("&#8230;weiterlesen", "");
                newsItem.setContent(content);
            }

            newsItems.add(newsItem);
        }

        return newsItems;
    }


    private List<String> parseUrls(News jNews) {
        List<String> urls = new ArrayList<>();
        for(News.Rendered rendered : jNews.getRendered()) {
            Document document = Jsoup.parse(rendered.getValue());
            for(Element a : document.select("a")) {
                String url = buildURL(a.attr("href"));
                if(!urls.contains(url)) {
                    urls.add(url);
                }
            }
        }
        return urls;
    }

    private String buildURL(String url) {
        return (url.startsWith("http://") || url.startsWith("https://") ? url : "https://effner.de" + url);
    }
}
