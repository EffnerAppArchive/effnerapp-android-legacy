package de.effnerapp.effner.ui.news.sections;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    public final String name;
    public final int id;

    public Item(String name, int id) {
        this.name = name;
        this.id = id;
    }

    protected Item(Parcel in) {
        name = in.readString();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(name);
    }
}
