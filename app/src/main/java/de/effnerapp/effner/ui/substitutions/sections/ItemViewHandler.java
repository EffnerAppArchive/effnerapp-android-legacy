package de.effnerapp.effner.ui.substitutions.sections;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;

public class ItemViewHandler extends ChildViewHolder {

    private TextView mTextView;

    public ItemViewHandler(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.recycler_textview);

    }

    public void bind(Item item) {
        mTextView.setText(item.name);
        if(SplashActivity.sharedPreferences.getBoolean("APP_DESIGN_DARK", false)) {
            mTextView.setTextColor(Color.WHITE);
        }
    }
}
