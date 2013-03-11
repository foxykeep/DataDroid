/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.model;

import com.foxykeep.datadroidpoc.data.provider.PoCContent.DbPerson;

import android.content.ContentValues;

public final class Person {

    public String firstName;
    public String lastName;
    public String email;
    public String city;
    public int postalCode;
    public int age;
    public boolean isWorking;

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbPerson.Columns.FIRST_NAME.getName(), firstName);
        cv.put(DbPerson.Columns.LAST_NAME.getName(), lastName);
        cv.put(DbPerson.Columns.EMAIL.getName(), email);
        cv.put(DbPerson.Columns.CITY.getName(), city);
        cv.put(DbPerson.Columns.POSTAL_CODE.getName(), postalCode);
        cv.put(DbPerson.Columns.AGE.getName(), age);
        cv.put(DbPerson.Columns.IS_WORKING.getName(), isWorking);
        return cv;
    }
}
