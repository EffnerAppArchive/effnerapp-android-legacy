/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.fragments.exams;

import android.content.Context;
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
import de.effnerapp.effner.data.api.json.data.Exam;

public class ExamItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<Exam> exams;
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    ExamItemAdapter(Context context, List<Exam> exams) {
        this.context = context;
        this.exams = exams;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // create a new view
        if (viewType == 0) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam, parent, false));
        } else {
            return new InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disclaimer_exams, parent, false));
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder.getItemViewType() == 0) {
            ItemViewHolder iHolder = (ItemViewHolder) holder;
            String text = exams.get(i).getName();
            String date = exams.get(i).getDate();
            Date sDate = null;
            try {
                sDate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert sDate != null;
            if (sDate.after(new Date())) {
                iHolder.itemCard.getBackground().setAlpha(255);

                iHolder.dateText.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                iHolder.itemCard.getBackground().setAlpha(100);
                iHolder.dateText.setTextColor(context.getResources().getColor(R.color.red));
            }
            iHolder.dateText.setText(date);
            iHolder.itemText.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return exams.size() + 1;
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