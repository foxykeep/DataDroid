/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.data.provider;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

import com.foxykeep.dataproxy.provider.DatabaseContent;
import com.foxykeep.dataproxypoc.skeleton.data.provider.SkeletonProvider;
import com.foxykeep.dataproxypoc.skeleton.data.provider.SkeletonContent.Skeleton;

/**
 * {@link PoCContent} is the superclass of the various classes of content stored
 * by {@link SkeletonProvider}. It adds to {@link DatabaseContent} the
 * {@link #AUTHORITY} and {@link #CONTENT_URI}
 * <p>
 * <b>This class is a skeleton of the normal class. Replace the TODOs by your
 * code</b>
 * </p>
 */
public abstract class PoCContent extends DatabaseContent {
    public static final Uri CONTENT_URI = Uri.parse("content://" + PoCProvider.AUTHORITY);

    public interface PersonColumns {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String EMAIL = "email";
        public static final String CITY = "city";
        public static final String POSTAL_CODE = "postalCode";
        public static final String AGE = "age";
        public static final String IS_WORKING = "isWorking";
    }

    public static final class Person extends PoCContent implements PersonColumns {
        public static final String TABLE_NAME = "person";
        public static final Uri CONTENT_URI = Uri.parse(PoCContent.CONTENT_URI + "/" + TABLE_NAME);

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
                RECORD_ID, PersonColumns.FIRST_NAME, PersonColumns.LAST_NAME, PersonColumns.EMAIL, PersonColumns.CITY,
                PersonColumns.POSTAL_CODE, PersonColumns.AGE, PersonColumns.IS_WORKING
        };

        public static final int CONTENT_NAME_ID_COLUMN = 0;
        public static final int CONTENT_NAME_FIRST_NAME_COLUMN = 1;
        public static final int CONTENT_NAME_LAST_NAME_COLUMN = 2;
        public static final int CONTENT_NAME_AGE_COLUMN = 3;
        public static final String[] CONTENT_NAME_PROJECTION = new String[] {
                RECORD_ID, PersonColumns.FIRST_NAME, PersonColumns.LAST_NAME, PersonColumns.AGE
        };

        static void createTable(final SQLiteDatabase db) {
            final String s = " (" + DatabaseContent.RECORD_ID + " integer primary key autoincrement, "
                    + PersonColumns.FIRST_NAME + " text, " + PersonColumns.FIRST_NAME + " text, " + PersonColumns.EMAIL
                    + " text, " + PersonColumns.CITY + " text, " + PersonColumns.POSTAL_CODE + " integer, "
                    + PersonColumns.AGE + " integer, " + PersonColumns.IS_WORKING + " integer " + ");";

            db.execSQL("create table " + Skeleton.TABLE_NAME + s);

            // TODO : Add the table's indexes (if any) using the
            // getCreateIndexString() method
            db.execSQL(getCreateIndexString(TABLE_NAME, PersonColumns.LAST_NAME));

            // TODO : Add the table's triggers (if any)
        }

        static void upgradeTable(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            try {
                db.execSQL("drop table " + Skeleton.TABLE_NAME);
            } catch (final SQLException e) {
            }
            createTable(db);
        }

        public static String getBulkInsertString() {
            final StringBuffer sqlRequest = new StringBuffer("INSERT INTO ");
            sqlRequest.append(Person.TABLE_NAME);
            sqlRequest.append(" ( ");
            sqlRequest.append(PersonColumns.FIRST_NAME);
            sqlRequest.append(", ");
            sqlRequest.append(PersonColumns.LAST_NAME);
            sqlRequest.append(", ");
            sqlRequest.append(PersonColumns.EMAIL);
            sqlRequest.append(", ");
            sqlRequest.append(PersonColumns.CITY);
            sqlRequest.append(", ");
            sqlRequest.append(PersonColumns.POSTAL_CODE);
            sqlRequest.append(", ");
            sqlRequest.append(PersonColumns.AGE);
            sqlRequest.append(", ");
            sqlRequest.append(PersonColumns.IS_WORKING);
            sqlRequest.append(" ) ");
            sqlRequest.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
            return sqlRequest.toString();
        }

        public static void bindValuesInBulkInsert(final SQLiteStatement stmt, final ContentValues values) {
            int i = 1;
            String value = values.getAsString(PersonColumns.FIRST_NAME);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(PersonColumns.LAST_NAME);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(PersonColumns.EMAIL);
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(PersonColumns.CITY);
            stmt.bindString(i++, value != null ? value : "");
            stmt.bindLong(i++, values.getAsInteger(PersonColumns.POSTAL_CODE));
            stmt.bindLong(i++, values.getAsInteger(PersonColumns.AGE));
            stmt.bindLong(i++, values.getAsInteger(PersonColumns.IS_WORKING));
        }
    }
}
