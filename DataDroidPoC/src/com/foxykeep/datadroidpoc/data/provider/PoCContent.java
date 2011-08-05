/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.provider;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;

import com.foxykeep.datadroid.provider.util.DatabaseUtil;
import com.foxykeep.datadroidpoc.data.model.Person;
import com.foxykeep.datadroidpoc.data.model.Phone;

/**
 * {@link PoCContent} is the superclass of the various classes of content stored
 * by {@link PoCProvider}. It adds to {@link DatabaseContent} the
 * {@link #AUTHORITY} and {@link #CONTENT_URI}
 * <p>
 * </p>
 */
public abstract class PoCContent {
    public static final Uri CONTENT_URI = Uri.parse("content://" + PoCProvider.AUTHORITY);

    public interface PersonDaoColumns {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String EMAIL = "email";
        public static final String CITY = "city";
        public static final String POSTAL_CODE = "postalCode";
        public static final String AGE = "age";
        public static final String IS_WORKING = "isWorking";
    }

    public static final class PersonDao extends PoCContent implements PersonDaoColumns, BaseColumns {
        public static final String TABLE_NAME = "person";
        public static final Uri CONTENT_URI = Uri.parse(PoCContent.CONTENT_URI + "/" + TABLE_NAME);
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/com.foxykeep.datadroidpoc.data.provider.person";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/com.foxykeep.datadroidpoc.data.provider.person";

        public static final String MIN_AGE_SELECTION = AGE + " >= ?";
        public static final String LAST_NAME_ORDER_BY = LAST_NAME + " ASC";

        public static final int CONTENT_ID_COLUMN = 0;
        public static final int CONTENT_FIRST_NAME_COLUMN = 1;
        public static final int CONTENT_LAST_NAME_COLUMN = 2;
        public static final int CONTENT_EMAIL_COLUMN = 3;
        public static final int CONTENT_CITY_COLUMN = 4;
        public static final int CONTENT_POSTAL_CODE_COLUMN = 5;
        public static final int CONTENT_AGE_COLUMN = 6;
        public static final int CONTENT_IS_WORKING_COLUMN = 7;
        public static final String[] CONTENT_PROJECTION = new String[] {
                _ID, FIRST_NAME, LAST_NAME, EMAIL, CITY, POSTAL_CODE, AGE, IS_WORKING
        };

        public static final int CONTENT_NAME_ID_COLUMN = 0;
        public static final int CONTENT_NAME_FIRST_NAME_COLUMN = 1;
        public static final int CONTENT_NAME_LAST_NAME_COLUMN = 2;
        public static final int CONTENT_NAME_AGE_COLUMN = 3;
        public static final String[] CONTENT_NAME_PROJECTION = new String[] {
                _ID, FIRST_NAME, LAST_NAME, AGE
        };

        static void createTable(final SQLiteDatabase db) {
            final String s = " (" + _ID + " integer primary key autoincrement, " + FIRST_NAME + " text, " + LAST_NAME
                    + " text, " + EMAIL + " text, " + CITY + " text, " + POSTAL_CODE + " integer, " + AGE
                    + " integer, " + IS_WORKING + " integer " + ");";

            db.execSQL("create table " + TABLE_NAME + s);

            db.execSQL(DatabaseUtil.getCreateIndexString(TABLE_NAME, LAST_NAME));
        }

        static void upgradeTable(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            try {
                db.execSQL("drop table " + TABLE_NAME);
            } catch (final SQLException e) {
            }
            createTable(db);
        }

        public static String getBulkInsertString() {
            final StringBuffer sqlRequest = new StringBuffer("INSERT INTO ");
            sqlRequest.append(TABLE_NAME);
            sqlRequest.append(" ( ");
            sqlRequest.append(FIRST_NAME);
            sqlRequest.append(", ");
            sqlRequest.append(LAST_NAME);
            sqlRequest.append(", ");
            sqlRequest.append(EMAIL);
            sqlRequest.append(", ");
            sqlRequest.append(CITY);
            sqlRequest.append(", ");
            sqlRequest.append(POSTAL_CODE);
            sqlRequest.append(", ");
            sqlRequest.append(AGE);
            sqlRequest.append(", ");
            sqlRequest.append(IS_WORKING);
            sqlRequest.append(" ) ");
            sqlRequest.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
            return sqlRequest.toString();
        }

        public static void bindValuesInBulkInsert(final SQLiteStatement stmt, final ContentValues values) {
            int i = 1;
            String value = values.getAsString(FIRST_NAME);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(LAST_NAME);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(EMAIL);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(CITY);
            stmt.bindString(i++, value != null ? value : "");
            stmt.bindLong(i++, values.getAsInteger(POSTAL_CODE));
            stmt.bindLong(i++, values.getAsInteger(AGE));
            stmt.bindLong(i++, values.getAsInteger(IS_WORKING));
        }

        public static ContentValues getContentValues(final Person person) {
            ContentValues values = new ContentValues();
            values.put(FIRST_NAME, person.firstName);
            values.put(LAST_NAME, person.lastName);
            values.put(EMAIL, person.email);
            values.put(CITY, person.city);
            values.put(POSTAL_CODE, person.postalCode);
            values.put(AGE, person.age);
            values.put(IS_WORKING, person.isWorking ? 1 : 0);
            return values;
        }
    }

    public interface PhoneDaoColumns {
        public static final String SERVER_ID = "serverId";
        public static final String NAME = "name";
        public static final String MANUFACTURER = "manufacturer";
        public static final String ANDROID_VERSION = "androidVersion";
        public static final String SCREEN_SIZE = "screenSize";
        public static final String PRICE = "price";
    }

    public static final class PhoneDao extends PoCContent implements PhoneDaoColumns, BaseColumns {
        public static final String TABLE_NAME = "phone";
        public static final Uri CONTENT_URI = Uri.parse(PoCContent.CONTENT_URI + "/" + TABLE_NAME);
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/com.foxykeep.datadroidpoc.data.provider.phone";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/com.foxykeep.datadroidpoc.data.provider.phone";

        public static final String NAME_ORDER_BY = NAME + " ASC";

        public static final int CONTENT_ID_COLUMN = 0;
        public static final int CONTENT_SERVER_ID_COLUMN = 1;
        public static final int CONTENT_NAME_COLUMN = 2;
        public static final int CONTENT_MANUFACTURER_COLUMN = 3;
        public static final int CONTENT_ANDROID_VERSION_COLUMN = 4;
        public static final int CONTENT_SCREEN_SIZE_COLUMN = 5;
        public static final int CONTENT_PRICE_COLUMN = 6;
        public static final String[] CONTENT_PROJECTION = new String[] {
                _ID, SERVER_ID, NAME, MANUFACTURER, ANDROID_VERSION, SCREEN_SIZE, PRICE
        };

        public static final int CONTENT_LIST_ID_COLUMN = 0;
        public static final int CONTENT_LIST_NAME_COLUMN = 1;
        public static final int CONTENT_LIST_MANUFACTURER_COLUMN = 2;
        public static final String[] CONTENT_LIST_PROJECTION = new String[] {
                _ID, NAME, MANUFACTURER
        };

        static void createTable(final SQLiteDatabase db) {
            final String s = " (" + _ID + " integer primary key autoincrement, " + SERVER_ID + " integer, " + NAME
                    + " text, " + MANUFACTURER + " text, " + ANDROID_VERSION + " text, " + SCREEN_SIZE + " text, "
                    + PRICE + " integer );";

            db.execSQL("create table " + TABLE_NAME + s);

            db.execSQL(DatabaseUtil.getCreateIndexString(TABLE_NAME, NAME));

        }

        static void upgradeTable(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            try {
                db.execSQL("drop table " + TABLE_NAME);
            } catch (final SQLException e) {
            }
            createTable(db);
        }

        public static String getBulkInsertString() {
            final StringBuffer sqlRequest = new StringBuffer("INSERT INTO ");
            sqlRequest.append(TABLE_NAME);
            sqlRequest.append(" ( ");
            sqlRequest.append(SERVER_ID);
            sqlRequest.append(", ");
            sqlRequest.append(NAME);
            sqlRequest.append(", ");
            sqlRequest.append(MANUFACTURER);
            sqlRequest.append(", ");
            sqlRequest.append(ANDROID_VERSION);
            sqlRequest.append(", ");
            sqlRequest.append(SCREEN_SIZE);
            sqlRequest.append(", ");
            sqlRequest.append(PRICE);
            sqlRequest.append(" ) ");
            sqlRequest.append(" VALUES (?, ?, ?, ?, ?, ?)");
            return sqlRequest.toString();
        }

        public static void bindValuesInBulkInsert(final SQLiteStatement stmt, final ContentValues values) {
            int i = 1;
            stmt.bindLong(i++, values.getAsInteger(SERVER_ID));
            String value = values.getAsString(NAME);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(MANUFACTURER);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(ANDROID_VERSION);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(SCREEN_SIZE);
            stmt.bindString(i++, value != null ? value : "");
            stmt.bindLong(i++, values.getAsInteger(PRICE));
        }

        public static ContentValues getContentValues(final Phone phone) {
            ContentValues values = new ContentValues();
            values.put(SERVER_ID, phone.serverId);
            values.put(NAME, phone.name);
            values.put(MANUFACTURER, phone.manufacturer);
            values.put(ANDROID_VERSION, phone.androidVersion);
            values.put(SCREEN_SIZE, phone.screenSize);
            values.put(PRICE, phone.price);
            return values;
        }
    }
}
