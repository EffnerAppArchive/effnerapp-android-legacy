package de.effnerapp.effner.ui.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;

public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.ItemViewHolder> {
    private Context context;
    private ViewGroup parent;
    private List<NewsItem> news;
    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    NewsItemAdapter(List<NewsItem> news) {
        this.news = news;
    }

    @NotNull
    @Override
    public NewsItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        this.context = parent.getContext();
        this.parent = parent;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        String title = news.get(i).getTitle();
        String content = news.get(i).getContent();
        holder.itemLayout.setBackgroundColor(SplashActivity.getDataStack().getColorByKey("COLOR_SECONDARY").getColorValue());
        holder.title.setText(title);
        holder.content.setText(content);

        holder.bottomLayout.removeAllViews();
        for(String url : news.get(i).getUrls()) {
            View v = LayoutInflater.from(context).inflate(R.layout.news_document, parent, false);
            CardView docCard = v.findViewById(R.id.doc_card);
            LinearLayout docLayout = v.findViewById(R.id.doc_layout);
            TextView docTitle = v.findViewById(R.id.doc_title);
            docTitle.setText(url.endsWith(".pdf") ? "Dokument" : "Internet-Adresse");
            docLayout.setBackgroundColor(SplashActivity.getDataStack().getColorByKey(url.endsWith(".pdf") ? "COLOR_STATIC_LIGHTGREEN" : "COLOR_STATIC_LIGHTBLUE").getColorValue());
            docCard.setOnClickListener(v1 -> MainActivity.getInstance().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
            holder.bottomLayout.addView(v);
        }

    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parent;
        CardView itemCard;
        LinearLayout itemLayout;
        TextView title;
        TextView content;
        LinearLayout bottomLayout;

        ItemViewHolder(View view) {
            super(view);
            parent = view.findViewById(R.id.parent);
            itemCard = view.findViewById(R.id.item_card);
            itemLayout = view.findViewById(R.id.item_layout);
            title = view.findViewById(R.id.news_title);
            content = view.findViewById(R.id.news_content);
            bottomLayout = view.findViewById(R.id.bottom_layout);
        }
    }
}