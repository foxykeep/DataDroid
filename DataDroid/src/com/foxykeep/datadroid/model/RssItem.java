package com.foxykeep.datadroid.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class RssItem implements Parcelable {

    public String title;
    public String link;
    public String commentsLink;
    public long publicationDate;

    public ArrayList<String> categoryList = new ArrayList<String>();

    public String description;
    public String encodedContext;

    // guid ?
    // Add the other xmlns ?

    // Parcelable management
    private RssItem(final Parcel in) {
        title = in.readString();
        link = in.readString();
        description = in.readString();
        publicationDate = in.readLong();

        final int categoryListSize = in.readInt();
        for (int i = 0; i < categoryListSize; i++) {
            categoryList.add(in.readString());
        }

        description = in.readString();
        encodedContext = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeLong(publicationDate);

        final int categoryListSize = categoryList.size();
        dest.writeInt(categoryListSize);
        for (int i = 0; i < categoryListSize; i++) {
            dest.writeString(categoryList.get(i));
        }

        dest.writeString(description);
        dest.writeString(encodedContext);
    }

    public static final Parcelable.Creator<RssItem> CREATOR = new Parcelable.Creator<RssItem>() {
        public RssItem createFromParcel(final Parcel in) {
            return new RssItem(in);
        }

        public RssItem[] newArray(final int size) {
            return new RssItem[size];
        }
    };
}
