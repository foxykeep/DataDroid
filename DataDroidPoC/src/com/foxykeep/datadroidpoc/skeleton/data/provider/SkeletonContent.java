/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.skeleton.data.provider;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.foxykeep.datadroid.provider.util.DatabaseUtil;

/**
 * {@link SkeletonContent} is the superclass of the various classes of content stored by {@link SkeletonProvider}.
 * <p>
 * <b>This class is a skeleton of the normal class. Replace the TODOs by your code</b>
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
        // TODO : Set the elem and dir types
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/com.foxykeep.datadroidpoc.skeleton.data.provider.Skeleton";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/com.foxykeep.datadroidpoc.skeleton.data.provider.Skeleton";

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
            final String s = " (" + _ID + " integer primary key autoincrement, " + COLUMN_NAME_ONE + " text, " + COLUMN_NAME_TWO + " integer, "
                    + COLUMN_NAME_THREE + " integer " + ");";

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
    }
}
