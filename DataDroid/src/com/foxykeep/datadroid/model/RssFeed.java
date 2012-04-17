package com.foxykeep.datadroid.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class RssFeed implements Parcelable {

    public String title;
    public String link;
    public String description;
    public long pubDate;
    public long lastBuildDate;
    public String language;

    public ArrayList<RssItem> rssItemList = new ArrayList<RssItem>();

    // Parcelable management
    private RssFeed(final Parcel in) {
        title = in.readString();
        link = in.readString();
        description = in.readString();
        pubDate = in.readLong();
        lastBuildDate = in.readLong();
        language = in.readString();

        in.readTypedList(rssItemList, RssItem.CREATOR);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeLong(pubDate);
        dest.writeLong(lastBuildDate);
        dest.writeString(language);

        dest.writeTypedList(rssItemList);
    }

    public static final Parcelable.Creator<RssFeed> CREATOR = new Parcelable.Creator<RssFeed>() {
        public RssFeed createFromParcel(final Parcel in) {
            return new RssFeed(in);
        }

        public RssFeed[] newArray(final int size) {
            return new RssFeed[size];
        }
    };
}
