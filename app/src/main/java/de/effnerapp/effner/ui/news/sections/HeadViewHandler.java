package de.effnerapp.effner.ui.news.sections;

import android.graphics.Color;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


public class HeadViewHandler extends GroupViewHolder {

    private TextView mTextView;
    private ImageView arrow;

    public HeadViewHandler(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.recycler_head_view);
        arrow = itemView.findViewById(R.id.arrow);
    }

    public void bind(Head head) {
        mTextView.setText(head.getTitle());
        if (head.getColor() != Color.BLACK) {
            mTextView.setTextColor(head.getColor());
            if (SplashActivity.sharedPreferences.getBoolean("APP_DESIGN_DARK", false)) {
                arrow.setColorFilter(Color.WHITE);
            }
        } else {
            if (SplashActivity.sharedPreferences.getBoolean("APP_DESIGN_DARK", false)) {
                mTextView.setTextColor(Color.WHITE);
                arrow.setColorFilter(Color.WHITE);
            }
        }
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360 + 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}