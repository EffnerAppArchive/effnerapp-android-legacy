package de.effnerapp.effner.ui.schooltests;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.model.Schooltest;

public class SchooltestItemAdapter extends RecyclerView.Adapter<SchooltestItemAdapter.ItemViewHolder> {
    private List<Schooltest> schooltests;
    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView dateCard;
        LinearLayout dateLayout;
        TextView dateText;
        CardView itemCard;
        LinearLayout itemLayout;
        TextView itemText;
        public ItemViewHolder(View view) {
            super(view);
            dateCard = view.findViewById(R.id.date_card);
            dateLayout = view.findViewById(R.id.date_layout);
            dateText = view.findViewById(R.id.term_date_view);
            itemCard = view.findViewById(R.id.item_card);
            itemLayout = view.findViewById(R.id.item_layout);
            itemText = view.findViewById(R.id.term_item_view);
        }
    }

    public SchooltestItemAdapter(List<Schooltest> schooltests) {
        this.schooltests = schooltests;
    }


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
        int light_red = Color.argb(150,255, 69, 69);
        int light_blue = Color.argb(150,89, 255, 249);
        int blue = Color.argb(150,112, 153, 255);
        int light_green = Color.argb(150,170, 255, 150);
        int light_yellow = Color.argb(150, 255, 215, 56);
        int orange = Color.argb(150, 255, 157, 59);
        int green = Color.argb(100,16, 158, 0);
        int red = Color.argb(100, 158, 0, 0);

        switch (schooltests.get(i).getType()) {
            case "SA":
                holder.itemLayout.setBackgroundColor(light_red);
                break;
            case "KA":
                holder.itemLayout.setBackgroundColor(orange);
                break;
            case "EX":
                holder.itemLayout.setBackgroundColor(blue);
                break;
            case "JGST":
                holder.itemLayout.setBackgroundColor(light_blue);
                break;
            case "TEST":
                holder.itemLayout.setBackgroundColor(light_green);
                break;
            case "Debatte":
                holder.itemLayout.setBackgroundColor(light_yellow);
                break;
        }
        Date sDate = null;
        try {
            sDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert sDate != null;
        if(sDate.after(new Date())) {
            holder.dateLayout.setBackgroundColor(green);
        } else {
            holder.itemLayout.getBackground().setAlpha(100);
            holder.dateLayout.setBackgroundColor(red);
        }
        holder.dateText.setText(date);
        holder.itemText.setText(text);

    }

    @Override
    public int getItemCount() {
        return schooltests.size();
    }
}