/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.skeleton.data.provider;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;

import com.foxykeep.dataproxy.provider.util.DatabaseUtil;

/**
 * {@link SkeletonContent} is the superclass of the various classes of content
 * stored by {@link SkeletonProvider}.
 * <p>
 * <b>This class is a skeleton of the normal class. Replace the TODOs by your
 * code</b>
 * </p>
 */
public abstract class SkeletonContent {
    // TODO : Set the SkeletonProvider authority
    public static final Uri CONTENT_URI = Uri.parse("content://" + SkeletonProvider.AUTHORITY);

    // TODO : Create an interface with the columns for your table
    public interface SkeletonColumns {
        public static final String COLUMN_NAME_ONE = "columnOne";
        public static final String COLUMN_NAME_TWO = "columnTwo";
        public static final String COLUMN_NAME_THREE = "columnThree";
    }

    public static final class Skeleton extends SkeletonContent implements SkeletonColumns, BaseColumns {
        // TODO : Set the table name
        public static final String TABLE_NAME = "skeleton";
        public static final Uri CONTENT_URI = Uri.parse(SkeletonContent.CONTENT_URI + "/" + TABLE_NAME);

        // TODO : Add constants for the selection/sort order you will use in
        // your project
        public static final String COLUMN_NAME_ONE_SELECTION = COLUMN_NAME_ONE + " = ?";
        public static final String COLUMN_NAME_TWO_ORDER_BY = COLUMN_NAME_TWO + " ASC";

        // TODO : This is the default projection which returns all the columns
        public static final int CONTENT_ID_COLUMN = 0;
        // TODO : Add incremental constant to get the columns in the content
        // projection below
        public static final int CONTENT_COLUMN_NAME_CONSTANT_ONE_COLUMN = 1;
        public static final int CONTENT_COLUMN_NAME_CONSTANT_TWO_COLUMN = 2;
        public static final int CONTENT_COLUMN_NAME_CONSTANT_THREE_COLUMN = 3;
        public static final String[] CONTENT_PROJECTION = new String[] {
                _ID, COLUMN_NAME_ONE, COLUMN_NAME_TWO, COLUMN_NAME_THREE
        };

        // TODO : Add the other projections (if any) you will use using the same
        // model as
        // the content projection

        // TODO : The following 2 methods are used for creation and upgrade of a
        // table.
        static void createTable(final SQLiteDatabase db) {
            final String s = " (" + _ID + " integer primary key autoincrement, " + COLUMN_NAME_ONE + " text, "
                    + COLUMN_NAME_TWO + " integer, " + COLUMN_NAME_THREE + " integer " + ");";

            db.execSQL("create table " + TABLE_NAME + s);

            // TODO : Add the table's indexes (if any) using the
            // getCreateIndexString() method
            db.execSQL(DatabaseUtil.getCreateIndexString(TABLE_NAME, COLUMN_NAME_ONE));

            // TODO : Add the table's triggers (if any)
        }

        static void upgradeTable(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            try {
                db.execSQL("drop table " + TABLE_NAME);
            } catch (final SQLException e) {
            }
            createTable(db);
        }

        // TODO : The 2 following methods allow to save in bulk (it should be
        // used if you have more than one insert to do)
        public static String getBulkInsertString() {
            final StringBuffer sqlRequest = new StringBuffer("INSERT INTO ");
            sqlRequest.append(TABLE_NAME);
            sqlRequest.append(" ( ");
            sqlRequest.append(COLUMN_NAME_ONE);
            sqlRequest.append(", ");
            sqlRequest.append(COLUMN_NAME_TWO);
            sqlRequest.append(", ");
            sqlRequest.append(COLUMN_NAME_THREE);
            sqlRequest.append(" ) ");
            sqlRequest.append(" VALUES (?, ?, ?)");
            return sqlRequest.toString();
        }

        public static void bindValuesInBulkInsert(final SQLiteStatement stmt, final ContentValues values) {
            final String columnOne = values.getAsString(COLUMN_NAME_ONE);
            stmt.bindString(1, columnOne != null ? columnOne : "");
            stmt.bindLong(2, values.getAsInteger(COLUMN_NAME_TWO));
            stmt.bindLong(3, values.getAsInteger(COLUMN_NAME_THREE));
        }
    }
}
