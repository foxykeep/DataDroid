/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class defining an RSS feed.
 * <p>
 * Fields are based on this definition : http://www.w3schools.com/rss/rss_channel.asp
 * <p>
 * If you need to add more fields, subclass this class and add your fields. (Don't forget to call
 * the corresponding <code>super</code> method in both constructors).
 * 
 * @author Foxykeep
 */
public class RssFeed implements Parcelable {

    // Required
    public String title;
    public String link;
    public String description;

    // Optional
    public long pubDate = -1;
    public long lastBuildDate = -1;

    public String language = null;
    public String copyright = null;
    public String generator = null;

    public String imageWebsiteLink = null;
    public String imageTitle = null;
    public String imageUrl = null;
    public int imageWidth = -1;
    public int imageHeight = -1;
    public String imageDescription = null;

    public String managingEditor = null;
    public String webmaster = null;

    /**
     * Array of days where the aggregators should skip updating.
     * <p>
     * 0 => monday, 1 => tuesday, ...
     */
    public int[] skipDayArray = null;
    /**
     * Array of hours where the aggregators should skip updating.
     * <p>
     * 0 => 00:00, 1 => 01:00, ..., 13 => 13:00, ...
     */
    public int[] skipHourArray = null;

    public int ttl = -1;

    public ArrayList<String> categoryList = new ArrayList<String>();

    public ArrayList<RssItem> rssItemList = new ArrayList<RssItem>();

    public RssFeed() {
    }

    // Parcelable management
    private RssFeed(final Parcel in) {
        title = in.readString();
        link = in.readString();
        description = in.readString();

        pubDate = in.readLong();
        lastBuildDate = in.readLong();

        language = in.readString();
        copyright = in.readString();
        generator = in.readString();

        imageWebsiteLink = in.readString();
        imageTitle = in.readString();
        imageUrl = in.readString();
        imageWidth = in.readInt();
        imageHeight = in.readInt();
        imageDescription = in.readString();

        managingEditor = in.readString();
        webmaster = in.readString();

        skipDayArray = in.createIntArray();
        skipHourArray = in.createIntArray();

        ttl = in.readInt();

        in.readStringList(categoryList);

        readRssItemList(in);
    }

    /**
     * Read the list of {@link RssItem} from the {@link Parcel}.
     * <p>
     * If you subclass {@link RssItem} in your project, subclass also {@link RssFeed} and modify
     * this method to use your CREATOR instead of the one from {@link RssItem}.
     * 
     * @param in The Parcel in which to read the list.
     */
    protected void readRssItemList(final Parcel in) {
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
        dest.writeString(copyright);
        dest.writeString(generator);

        dest.writeString(imageWebsiteLink);
        dest.writeString(imageTitle);
        dest.writeString(imageUrl);
        dest.writeInt(imageWidth);
        dest.writeInt(imageHeight);
        dest.writeString(imageDescription);

        dest.writeString(managingEditor);
        dest.writeString(webmaster);

        dest.writeIntArray(skipDayArray);
        dest.writeIntArray(skipHourArray);

        dest.writeInt(ttl);

        dest.writeStringList(categoryList);

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
