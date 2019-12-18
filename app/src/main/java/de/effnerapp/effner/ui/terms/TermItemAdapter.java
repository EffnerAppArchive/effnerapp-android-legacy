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

import java.util.List;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.model.Term;

public class TermItemAdapter extends RecyclerView.Adapter<TermItemAdapter.ItemViewHolder> {
    private List<Term> terms;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        LinearLayout linearLayout;
        TextView textView;
        ImageView pic;
        public ItemViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardview);
            linearLayout = view.findViewById(R.id.linearlayout);
            textView = view.findViewById(R.id.term_item_view);
            pic = view.findViewById(R.id.term_pic_view);
        }
    }

    public TermItemAdapter(List<Term> terms) {
        this.terms = terms;
    }


    @Override
    public TermItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_item, parent, false);
        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        int light_blue = Color.rgb(193, 230, 225);
        String text = terms.get(i).getDate() + ": " + terms.get(i).getName();
        holder.textView.setText(text);
        holder.linearLayout.setBackgroundColor(light_blue);
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }
}