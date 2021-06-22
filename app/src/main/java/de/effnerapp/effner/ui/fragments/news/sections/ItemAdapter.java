/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 13.06.21, 13:21.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.news.sections;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.card.MaterialCardView;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import de.effnerapp.effner.R;
import de.effnerapp.effner.tools.view.IntentHelper;
import de.effnerapp.effner.ui.activities.main.MainActivity;

public class ItemAdapter extends ExpandableRecyclerViewAdapter<HeadViewHandler, ItemViewHandler> {
    private final List<Head> heads;

    public ItemAdapter(List<Head> groups) {
        super(groups);
        heads = groups;
    }

    @Override
    public HeadViewHandler onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_head, parent, false);
        return new HeadViewHandler(view);
    }

    @Override
    public ItemViewHandler onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ItemViewHandler(view);
    }

    @Override
    public void onBindChildViewHolder(ItemViewHandler holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Item item = (Item) group.getItems().get(childIndex);
        holder.bind(item);
        holder.itemView.setOnClickListener(v -> {
            if (heads.get(item.id).getDocuments().size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                Context dialogContext = builder.getContext();
                LayoutInflater inflater = LayoutInflater.from(dialogContext);
                View alertView = inflater.inflate(R.layout.news_attachments, null);

                builder.setTitle(R.string.d_news_attached_documents);
                builder.setView(alertView);
                TableLayout tableLayout = alertView.findViewById(R.id.table);
                for (String document : heads.get(item.id).getDocuments()) {
                    TableRow tableRow = new TableRow(dialogContext);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tableRow.setLayoutParams(params);
                    tableRow.setPadding(50, 20, 0, 0);

                    View cardView = inflater.inflate(R.layout.news_document, (ViewGroup) tableRow, false);
                    TextView title = cardView.findViewById(R.id.doc_title);
                    ImageView icon = cardView.findViewById(R.id.doc_icon);

                    MaterialCardView card = cardView.findViewById(R.id.doc_card);

                    title.setText(getUrlText(document));
                    icon.setImageDrawable(getDocumentDrawable(dialogContext, document));

                    tableRow.addView(cardView);
                    card.setOnClickListener(view -> IntentHelper.openView(MainActivity.getInstance(), document));
                    tableLayout.addView(tableRow);
                }
                builder.setPositiveButton(R.string.d_button_close, null);
                builder.show();
            } else {
                Toast.makeText(MainActivity.getInstance(), R.string.t_no_attached_documents, Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onBindGroupViewHolder(HeadViewHandler holder, int flatPosition, ExpandableGroup group) {
        final Head head = (Head) group;
        holder.bind(head);
    }

    private String getUrlText(String url) {
        url = url.replaceAll("\\?.*", "");
        if (url.startsWith("mailto:")) {
            return url.replace("mailto:", "");
        } else if (url.contains("/")) {
            String[] split = url.split("/");
            return split[split.length - 1];
        } else {
            return url;
        }
    }

    private Drawable getDocumentDrawable(Context context, String url) {
        if (url.endsWith(".pdf")) {
            return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_insert_drive_file_black_24dp, null);
        } else if (url.startsWith("mailto:")) {
            return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_mail_black_24dp, null);
        } else if (url.startsWith("https://effner.de") || url.startsWith("http://effner.de")) {
            return ResourcesCompat.getDrawable(context.getResources(), R.drawable.app_logo, null);
        } else {
            return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_baseline_link_24, null);
        }
    }
}
