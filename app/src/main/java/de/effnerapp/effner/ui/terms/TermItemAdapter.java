package de.effnerapp.effner.ui.terms;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.model.Term;

public class TermItemAdapter extends RecyclerView.Adapter<TermItemAdapter.ItemViewHolder> {
    private List<Term> terms;
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

    public TermItemAdapter(List<Term> terms) {
        this.terms = terms;
    }


    @Override
    public TermItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        int light_blue = Color.rgb(193, 230, 225);
        int green = Color.argb(100,66, 219, 132);
        int red = Color.argb(100,240, 139, 139);
        
        String text = terms.get(i).getName();
        String date = terms.get(i).getDate();
        holder.dateText.setText(date);
        holder.itemText.setText(text);
        holder.dateLayout.setBackgroundColor(green);
        holder.itemLayout.setBackgroundColor(light_blue);

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
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }
}