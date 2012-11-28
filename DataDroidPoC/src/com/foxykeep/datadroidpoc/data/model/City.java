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

public final class City implements Parcelable {

    public String name;
    public String postalCode;
    public String state;
    public String country;

    public City() {
    }

    // Parcelable management
    private City(Parcel in) {
        name = in.readString();
        postalCode = in.readString();
        state = in.readString();
        country = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(postalCode);
        dest.writeString(state);
        dest.writeString(country);
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
