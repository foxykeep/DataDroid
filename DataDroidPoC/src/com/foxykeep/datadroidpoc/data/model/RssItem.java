/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class defining an RSS item.
 * <p>
 * Fields are based on this definition : http://www.w3schools.com/rss/rss_item.asp
 *
 * @author Foxykeep
 */
public final class RssItem implements Parcelable {

    // Required
    public String title;
    public String link;
    public String description;

    // Optional
    public String commentsLink = null;
    public long pubDate = -1;
    public String author = null;

    public String enclosureLink = null;
    public int enclosureSize = -1;
    public String enclosureMimeType = null;

    public String guid = null;
    public boolean isGuidPermaLink = false;

    public ArrayList<String> categoryList = new ArrayList<String>();

    public String encodedContext = null;

    public String sourceLink;
    public String sourceText;

    public RssItem() {

    }

    // Parcelable management
    private RssItem(final Parcel in) {
        title = in.readString();
        link = in.readString();
        description = in.readString();

        commentsLink = in.readString();
        pubDate = in.readLong();
        author = in.readString();

        enclosureLink = in.readString();
        enclosureSize = in.readInt();
        enclosureMimeType = in.readString();

        guid = in.readString();
        isGuidPermaLink = in.readInt() == 1;

        in.readStringList(categoryList);

        encodedContext = in.readString();

        sourceLink = in.readString();
        sourceText = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(description);

        dest.writeString(commentsLink);
        dest.writeLong(pubDate);
        dest.writeString(author);

        dest.writeString(enclosureLink);
        dest.writeInt(enclosureSize);
        dest.writeString(enclosureMimeType);

        dest.writeString(guid);
        dest.writeInt(isGuidPermaLink ? 1 : 0);

        dest.writeStringList(categoryList);

        dest.writeString(encodedContext);

        dest.writeString(sourceLink);
        dest.writeString(sourceText);
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
