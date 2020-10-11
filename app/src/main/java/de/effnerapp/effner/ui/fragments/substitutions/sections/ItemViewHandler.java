package de.effnerapp.effner.ui.fragments.substitutions.sections;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import de.effnerapp.effner.R;

public class ItemViewHandler extends ChildViewHolder {

    private final TextView mTextView;

    public ItemViewHandler(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.recycler_textview);

    }

    public void bind(Item item) {
        mTextView.setText(item.name);
    }
}
