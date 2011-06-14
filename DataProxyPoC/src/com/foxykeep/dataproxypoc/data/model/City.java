package com.foxykeep.dataproxypoc.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {

    public String name;
    public int postalCode;
    public int departementNumber; // TODO a traduire en anglais
    public String departementName; // TODO a traduire en anglais

    public City() {
    }

    // Parcelable management
    private City(final Parcel in) {
        name = in.readString();
        postalCode = in.readInt();
        departementNumber = in.readInt();
        departementName = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeInt(postalCode);
        dest.writeInt(departementNumber);
        dest.writeString(departementName);

    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        public City createFromParcel(final Parcel in) {
            return new City(in);
        }

        public City[] newArray(final int size) {
            return new City[size];
        }
    };
}
