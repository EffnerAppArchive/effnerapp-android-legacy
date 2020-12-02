package de.effnerapp.effner.ui.fragments.bus;

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

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.mvv.Departure;
import de.effnerapp.effner.data.utils.ApiClient;

public class DepartureItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Departure> departures;
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    DepartureItemAdapter(List<Departure> departures) {
        this.departures = departures;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // create a new view
        if (viewType == 0) {
            return new DepartureItemAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.departure_item, parent, false));
        } else {
            return new DepartureItemAdapter.InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.disclaimer_info, parent, false));
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder.getItemViewType() == 0) {
            DepartureItemAdapter.ItemViewHolder iHolder = (DepartureItemAdapter.ItemViewHolder) holder;
            String departurePlanned = departures.get(i).getDeparturePlanned();
            String departureLive = departures.get(i).getDepartureLive();

            int dP = Integer.parseInt(departurePlanned.replace(":", ""));
            int dL = Integer.parseInt(departureLive.replace(":", ""));

            if (dP >= dL) {
                iHolder.timeLive.setTextColor(ApiClient.getInstance().getData().getColorByKey("COLOR_STATIC_GREEN").getColorValue());
            } else {
                iHolder.timeLive.setTextColor(ApiClient.getInstance().getData().getColorByKey("COLOR_STATIC_RED").getColorValue());
            }
            iHolder.timePlanned.setText(departurePlanned);
            iHolder.timeLive.setText(departureLive);
            iHolder.line.setText(departures.get(i).getLine().getNumber());

            // very ugly
            String[] t = departures.get(i).getLine().getDirection().split(" ");
            String dT = t.length >= 2 ? t[0] + " " + t[1] : t[0];
            iHolder.direction.setText(dT);
            // end
        }
    }

    @Override
    public int getItemCount() {
        return departures.size() + 1;
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
        final TextView line;
        final TextView direction;
        final TextView timePlanned;
        final TextView timeLive;
        final CardView itemCard;
        final LinearLayout itemLayout;

        ItemViewHolder(@NonNull View view) {
            super(view);
            line = view.findViewById(R.id.departure_line);
            direction = view.findViewById(R.id.departure_direction);
            timePlanned = view.findViewById(R.id.departure_time_planned);
            timeLive = view.findViewById(R.id.departure_time_live);
            itemCard = view.findViewById(R.id.item_card);
            itemLayout = view.findViewById(R.id.item_layout);
        }
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {

        public InfoViewHolder(@NonNull View view) {
            super(view);
        }
    }
}