package de.effnerapp.effner.ui.news.sections;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;

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
            if(heads.get(item.id).getDocuments().size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                Context dialogContext = builder.getContext();
                LayoutInflater inflater = LayoutInflater.from(dialogContext);
                View alertView = inflater.inflate(R.layout.news_attachments, null);

                builder.setTitle("Angehängte Dokumente");
                builder.setView(alertView);
                TableLayout tableLayout = alertView.findViewById(R.id.table);
                for(String document : heads.get(item.id).getDocuments()){
                    TableRow tableRow = new TableRow(dialogContext);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tableRow.setLayoutParams(params);
                    tableRow.setPadding(50,20,0,0);

                    View cardView = inflater.inflate(R.layout.news_document, null);
                    TextView title = cardView.findViewById(R.id.doc_title);

                    title.setText(getDocumentType(document));
                    tableRow.addView(cardView);
                    tableRow.setOnClickListener(view -> MainActivity.getInstance().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(document))));
                    tableLayout.addView(tableRow);
                }
                builder.setPositiveButton("Schließen", null);
                builder.show();
            } else {
                Toast.makeText(MainActivity.getInstance(), "An diesen Beitrag wurden keine Dokumente angehängt!", Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onBindGroupViewHolder(HeadViewHandler holder, int flatPosition, ExpandableGroup group) {
        final Head head = (Head) group;
        holder.bind(head);
    }

    private String getDocumentType(String url) {
        if(url.endsWith(".pdf")) {
            return "PDF-Dokument";
        } else if(url.startsWith("mailto:")) {
            return "Email-Adresse";
        } else if(url.startsWith("https://effner.de")) {
            return "Effner.de-Seite";
        } else {
            return "Externe Internet-Adresse";
        }
    }
}
