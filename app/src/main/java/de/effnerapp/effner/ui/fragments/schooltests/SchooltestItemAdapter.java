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

public class SchooltestItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Schooltest> schooltests;
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    SchooltestItemAdapter(List<Schooltest> schooltests) {
        this.schooltests = schooltests;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // create a new view
        if (viewType == 0) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.term_item, parent, false));
        } else {
            return new InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.disclaimer_info, parent, false));
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder.getItemViewType() == 0) {
            ItemViewHolder iHolder = (ItemViewHolder) holder;
            String text = schooltests.get(i).getName();
            String date = schooltests.get(i).getDate();
            Date sDate = null;
            try {
                sDate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert sDate != null;
            if (sDate.after(new Date())) {
                iHolder.dateText.setTextColor(ApiClient.getInstance().getData().getColorByKey("COLOR_STATIC_GREEN").getColorValue());
            } else {
                iHolder.itemCard.getBackground().setAlpha(100);
                iHolder.dateText.setTextColor(ApiClient.getInstance().getData().getColorByKey("COLOR_STATIC_RED").getColorValue());
            }
            iHolder.dateText.setText(date);
            iHolder.itemText.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return schooltests.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != getItemCount() - 1) {
            return 0;
        } else {
            return 1;
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView dateText;
        final CardView itemCard;
        final LinearLayout itemLayout;
        final TextView itemText;

        ItemViewHolder(@NonNull View view) {
            super(view);
            dateText = view.findViewById(R.id.term_date_view);
            itemCard = view.findViewById(R.id.item_card);
            itemLayout = view.findViewById(R.id.item_layout);
            itemText = view.findViewById(R.id.term_item_view);
        }
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {

        public InfoViewHolder(@NonNull View view) {
            super(view);
        }
    }
}