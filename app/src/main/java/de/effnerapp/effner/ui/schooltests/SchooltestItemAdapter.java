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

import java.util.List;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.model.Schooltest;

public class SchooltestItemAdapter extends RecyclerView.Adapter<SchooltestItemAdapter.ItemViewHolder> {
    private List<Schooltest> schooltests;

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

    public SchooltestItemAdapter(List<Schooltest> schooltests) {
        this.schooltests = schooltests;
    }


    @Override
    public SchooltestItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        String text = schooltests.get(i).getDate() + ": " + schooltests.get(i).getName();
        int light_red = Color.rgb(242, 143, 143);
        int light_blue = Color.rgb(193, 230, 225);
        int blue = Color.rgb(88, 102, 130);
        int light_green = Color.rgb(154, 255, 120);
        int light_yellow = Color.rgb(249, 255, 130);
        int orange = Color.rgb(255, 145, 82);
        switch (schooltests.get(i).getType()) {
            case "SA":
                holder.linearLayout.setBackgroundColor(light_red);
                break;
            case "KA":
                holder.linearLayout.setBackgroundColor(orange);
                break;
            case "EX":
                holder.linearLayout.setBackgroundColor(blue);
                break;
            case "JGST":
                holder.linearLayout.setBackgroundColor(light_blue);
                break;
            case "TEST":
                holder.linearLayout.setBackgroundColor(light_green);
                break;
            case "Debatte":
                holder.linearLayout.setBackgroundColor(light_yellow);
                break;
        }


        holder.textView.setText(text);

        holder.cardView.setOnClickListener(view -> {

            Toast toast = Toast.makeText(view.getContext(), schooltests.get(i).getName(), Toast.LENGTH_SHORT);
            toast.show();
        });
    }

    @Override
    public int getItemCount() {
        return schooltests.size();
    }
}