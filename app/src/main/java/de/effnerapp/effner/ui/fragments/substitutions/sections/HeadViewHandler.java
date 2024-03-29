/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.ui.fragments.substitutions.sections;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import de.effnerapp.effner.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


public class HeadViewHandler extends GroupViewHolder {

    private final TextView mTextView;
    private final TextView classBadge;
    private final TextView subjectBadge;
    private final ImageView arrow;

    public HeadViewHandler(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.recycler_head_view);
        classBadge = itemView.findViewById(R.id.sub_head_badge_class);
        subjectBadge = itemView.findViewById(R.id.sub_head_badge_subject);
        arrow = itemView.findViewById(R.id.arrow);
    }

    public void bind(Head head) {
        mTextView.setText(head.getTitle());
        if (head.getColor() != Color.BLACK) {
            mTextView.setTextColor(head.getColor());
        }

        for(Badge badge : head.getBadges()) {
            if(badge.getId() == 0) {
                classBadge.setVisibility(View.VISIBLE);
                classBadge.setText(badge.getText());
                if(badge.getColor() != Color.WHITE) {
                    classBadge.setBackgroundTintList(ColorStateList.valueOf(badge.getColor()));
                }
            } else if(badge.getId() == 1) {
                subjectBadge.setVisibility(View.VISIBLE);
                subjectBadge.setText(badge.getText());
                if(badge.getColor() != Color.WHITE) {
                    subjectBadge.setBackgroundTintList(ColorStateList.valueOf(badge.getColor()));
                }
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
        RotateAnimation rotate = new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate = new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
