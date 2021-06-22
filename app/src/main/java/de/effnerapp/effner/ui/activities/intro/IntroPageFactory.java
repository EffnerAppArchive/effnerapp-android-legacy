/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 22.06.21, 19:43.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.activities.intro;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;

import com.github.appintro.AppIntroFragment;

public class IntroPageFactory {

    public static class Builder {
        private String title, description;

        @ColorInt
        private int backgroundColor, titleColor, descriptionColor;

        @FontRes
        private int titleTypefaceFontRes, descriptionTypefaceFontRes;

        @DrawableRes
        private int imageDrawable, backgroundDrawable;

        public Builder() {
        }

        public Builder(String title) {
            this.title = title;
        }

        public Builder(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder backgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder titleColor(@ColorInt int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder descriptionColor(@ColorInt int descriptionColor) {
            this.descriptionColor = descriptionColor;
            return this;
        }

        public Builder titleTypefaceFontRes(@FontRes int titleTypefaceFontRes) {
            this.titleTypefaceFontRes = titleTypefaceFontRes;
            return this;
        }

        public Builder descriptionTypefaceFontRes(@FontRes int descriptionTypefaceFontRes) {
            this.descriptionTypefaceFontRes = descriptionTypefaceFontRes;
            return this;
        }

        public Builder imageDrawable(@DrawableRes int imageDrawable) {
            this.imageDrawable = imageDrawable;
            return this;
        }

        public Builder backgroundDrawable(@DrawableRes int backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
            return this;
        }

        public AppIntroFragment build() {
            return AppIntroFragment.newInstance(
                    title,
                    description,
                    imageDrawable,
                    backgroundColor,
                    titleColor,
                    descriptionColor,
                    titleTypefaceFontRes,
                    descriptionTypefaceFontRes,
                    backgroundDrawable
            );
        }

    }
}
