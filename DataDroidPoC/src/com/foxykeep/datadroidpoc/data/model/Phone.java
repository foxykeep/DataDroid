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

public final class Phone implements Parcelable {

    public long serverId;
    public String name;
    public String manufacturer;
    public String androidVersion;
    public double screenSize;
    public int price;

    public Phone() {}

    // Parcelable management
    private Phone(Parcel in) {
        serverId = in.readLong();
        name = in.readString();
        manufacturer = in.readString();
        androidVersion = in.readString();
        screenSize = in.readDouble();
        price = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(serverId);
        dest.writeString(name);
        dest.writeString(manufacturer);
        dest.writeString(androidVersion);
        dest.writeDouble(screenSize);
        dest.writeInt(price);

    }

    public static final Parcelable.Creator<Phone> CREATOR = new Parcelable.Creator<Phone>() {
        public Phone createFromParcel(Parcel in) {
            return new Phone(in);
        }

        public Phone[] newArray(int size) {
            return new Phone[size];
        }
    };
}
