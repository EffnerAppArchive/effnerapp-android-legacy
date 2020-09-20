package de.effnerapp.effner.ui.news;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.data.utils.NewsParser;
import de.effnerapp.effner.ui.news.sections.Head;
import de.effnerapp.effner.ui.news.sections.Item;
import de.effnerapp.effner.ui.news.sections.ItemAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    private final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.news_recycler_view);

        List<Head> heads = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        List<NewsItem> news = new NewsParser().parse(SplashActivity.getDataStack().getNews());
        int i = 0;
        for(NewsItem newsItem : news) {
            String date = parseDate(newsItem.getDate());
            String title = null, content;
            if(newsItem.getContent() == null || newsItem.getContent().isEmpty()) {
                content = newsItem.getTitle();
                title = "Ohne Titel - " + date;
            } else if(newsItem.getTitle() == null || newsItem.getTitle().isEmpty()) {
                content = newsItem.getContent();
            } else {
                content = newsItem.getContent();
                title = newsItem.getTitle();
            }
            content += "\n\nVeröffentlicht am " + date;
            Item item = new Item(content, i);
            heads.add(new Head(title, Collections.singletonList(item), newsItem.getUrls(), Color.BLACK));
            i++;
        }
        ItemAdapter adapter = new ItemAdapter(heads);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private String parseDate(String date) {
        try {
            return formatter.format(Objects.requireNonNull(parser.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
