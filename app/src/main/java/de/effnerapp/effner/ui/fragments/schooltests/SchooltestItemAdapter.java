package de.effnerapp.effner.ui.fragments.schooltests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.model.Schooltest;
import de.effnerapp.effner.data.utils.ApiClient;

public class SchooltestItemAdapter extends RecyclerView.Adapter<SchooltestItemAdapter.ItemViewHolder> {
    private final List<Schooltest> schooltests;
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    SchooltestItemAdapter(List<Schooltest> schooltests) {
        this.schooltests = schooltests;
    }

    @NotNull
    @Override
    public SchooltestItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        String text = schooltests.get(i).getName();
        String date = schooltests.get(i).getDate();
        holder.itemLayout.setBackgroundColor(ApiClient.getInstance().getData().getColorByKey("COLOR_ITEMS_" + schooltests.get(i).getType().toUpperCase()).getColorValue());
        Date sDate = null;
        try {
            sDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert sDate != null;
        if (sDate.after(new Date())) {
            holder.dateLayout.setBackgroundColor(ApiClient.getInstance().getData().getColorByKey("COLOR_STATIC_GREEN").getColorValue());
        } else {
            holder.itemLayout.getBackground().setAlpha(100);
            holder.dateLayout.setBackgroundColor(ApiClient.getInstance().getData().getColorByKey("COLOR_STATIC_RED").getColorValue());
        }
        holder.dateText.setText(date);
        holder.itemText.setText(text);

    }

    @Override
    public int getItemCount() {
        return schooltests.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final CardView dateCard;
        final LinearLayout dateLayout;
        final TextView dateText;
        final CardView itemCard;
        final LinearLayout itemLayout;
        final TextView itemText;

        ItemViewHolder(View view) {
            super(view);
            dateCard = view.findViewById(R.id.date_card);
            dateLayout = view.findViewById(R.id.date_layout);
            dateText = view.findViewById(R.id.term_date_view);
            itemCard = view.findViewById(R.id.item_card);
            itemLayout = view.findViewById(R.id.item_layout);
            itemText = view.findViewById(R.id.term_item_view);
        }
    }
}