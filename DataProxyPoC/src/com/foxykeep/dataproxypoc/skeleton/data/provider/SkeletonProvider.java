/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.skeleton.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.util.Log;

import com.foxykeep.dataproxy.provider.DatabaseContent;
import com.foxykeep.dataproxypoc.config.LogConfig;
import com.foxykeep.dataproxypoc.skeleton.data.provider.SkeletonContent.Skeleton;

// TODO : Change the className
public class SkeletonProvider extends ContentProvider {

    private static final String LOG_TAG = SkeletonProvider.class.getSimpleName();

    // TODO : Set the database name
    protected static final String DATABASE_NAME = "SkeletonProvider.db";

    // Any changes to the database format *must* include update-in-place code.
    // Original version: 1
    public static final int DATABASE_VERSION = 1;

    // TODO : Set the authority
    public static final String AUTHORITY = "com.foxykeep.dataproxypoc.skeleton.data.provider.SkeletonProvider";

    // TODO : Set the content class name
    public static final Uri INTEGRITY_CHECK_URI = Uri.parse("content://" + AUTHORITY + "/integrityCheck");

    // TODO : Create the uri constants using the model
    private static final int SKELETON_BASE = 0;
    private static final int SKELETON = SKELETON_BASE;
    private static final int SKELETON_ID = SKELETON_BASE + 1;

    // TODO : Example for the next table
    // private static final int PERIOD_BASE = 0x1000;
    // private static final int PERIOD = PERIOD_BASE;
    // private static final int PERIOD_ID = PERIOD_BASE + 1;

    private static final int BASE_SHIFT = 12; // DO NOT TOUCH ! 12 bits to the
    // base type: 0,
    // 0x1000, 0x2000, etc.

    // TODO : add the table names
    private static final String[] TABLE_NAMES = {
        Skeleton.TABLE_NAME
    };

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        final UriMatcher matcher = sURIMatcher;

        // TODO : Add a match for each possible call
        // All skeletons
        matcher.addURI(AUTHORITY, Skeleton.TABLE_NAME, SKELETON);
        // A specific skeleton
        matcher.addURI(AUTHORITY, Skeleton.TABLE_NAME + "/#", SKELETON_ID);
    }

    private SQLiteDatabase mDatabase;

    public synchronized SQLiteDatabase getDatabase(final Context context) {
        // Always return the cached database, if we've got one
        if (mDatabase != null) {
            return mDatabase;
        }

        final DatabaseHelper helper = new DatabaseHelper(context, DATABASE_NAME);
        mDatabase = helper.getWritableDatabase();
        if (mDatabase != null) {
            mDatabase.setLockingEnabled(true);
        }

        return mDatabase;
    }

    static SQLiteDatabase getReadableDatabase(final Context context) {
        final DatabaseHelper helper = new SkeletonProvider().new DatabaseHelper(context, DATABASE_NAME);
        return helper.getReadableDatabase();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(final Context context, final String name) {
            super(context, name, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            Log.d(LOG_TAG, "Creating database");

            // TODO : Add the calls to the methods created in SkeletonContent
            // Creates all tables here; each class has its own method
            if (LogConfig.DEBUG_LOGS_ENABLED) {
                Log.d(LOG_TAG, "Skeleton | createTable start");
            }
            Skeleton.createTable(db);
            if (LogConfig.DEBUG_LOGS_ENABLED) {
                Log.d(LOG_TAG, "Skeleton | createSkeletonTable end");
            }
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            // TODO : Add the calls to the methods created in SkeletonContent if
            // needed
        }

        @Override
        public void onOpen(final SQLiteDatabase db) {
        }
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {

        final int match = sURIMatcher.match(uri);
        final Context context = getContext();

        // Pick the correct database for this operation
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        String id = "0";

        if (LogConfig.DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "delete: uri=" + uri + ", match is " + match);
        }

        int result = -1;

        // TODO : Add the "case" lines depending on your matcher
        switch (match) {
            case SKELETON_ID:
                // case PROGRAM_ID:
                id = uri.getPathSegments().get(1);
                result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
                break;
            case SKELETON:
                // case PROGRAM:
                result = db.delete(TABLE_NAMES[table], selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public String getType(final Uri uri) {
        final int match = sURIMatcher.match(uri);
        // TODO : Add lines for your tables
        switch (match) {
            case SKELETON_ID:
                return "vnd.android.cursor.item/skeleton";
            case SKELETON:
                return "vnd.android.cursor.dir/skeleton";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {

        final int match = sURIMatcher.match(uri);
        final Context context = getContext();

        // Pick the correct database for this operation
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        long id;

        if (LogConfig.DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "insert: uri=" + uri + ", match is " + match);
        }

        Uri resultUri = null;

        // TODO : Add the "case" lines depending on your matcher
        switch (match) {
            case SKELETON:
                // case PROGRAM:
                id = db.insert(TABLE_NAMES[table], "foo", values);
                resultUri = ContentUris.withAppendedId(uri, id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Notify with the base uri, not the new uri (nobody is watching a new
        // record)
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {

        final int match = sURIMatcher.match(uri);
        final Context context = getContext();

        // Pick the correct database for this operation
        final SQLiteDatabase db = getDatabase(context);

        if (LogConfig.DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "bulkInsert: uri=" + uri + ", match is " + match);
        }

        int numberInserted = 0;
        SQLiteStatement insertStmt;

        // TODO : Add the "case" lines depending on your matcher
        db.beginTransaction();
        try {
            switch (match) {
                case SKELETON:
                    insertStmt = db.compileStatement(Skeleton.getBulkInsertString());
                    for (final ContentValues value : values) {
                        Skeleton.bindValuesInBulkInsert(insertStmt, value);
                        insertStmt.execute();
                        insertStmt.clearBindings();
                    }
                    insertStmt.close();
                    db.setTransactionSuccessful();
                    numberInserted = values.length;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } finally {
            db.endTransaction();
        }

        // Notify with the base uri, not the new uri (nobody is watching a new
        // record)
        context.getContentResolver().notifyChange(uri, null);
        return numberInserted;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs,
            final String sortOrder) {

        Cursor c = null;
        // TODO : Change the SkeletonContent into your class name
        final Uri notificationUri = SkeletonContent.CONTENT_URI;
        final int match = sURIMatcher.match(uri);
        final Context context = getContext();
        // Pick the correct database for this operation
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        String id;

        if (LogConfig.DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "query: uri=" + uri + ", match is " + match);
        }

        // TODO : Add the "case" lines depending on your matcher
        switch (match) {
            case SKELETON_ID:
                // case PROGRAM_ID:
                id = uri.getPathSegments().get(1);
                c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection), selectionArgs, null, null,
                        sortOrder);
                break;
            case SKELETON:
                // case PROGRAM:
                c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if ((c != null) && !isTemporary()) {
            c.setNotificationUri(getContext().getContentResolver(), notificationUri);
        }
        return c;
    }

    private String whereWithId(final String id, final String selection) {
        final StringBuilder sb = new StringBuilder(256);
        sb.append(DatabaseContent.RECORD_ID);
        sb.append(" = ");
        sb.append(id);
        if (selection != null) {
            sb.append(" AND (");
            sb.append(selection);
            sb.append(')');
        }
        return sb.toString();
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {

        final int match = sURIMatcher.match(uri);
        final Context context = getContext();
        // Pick the correct database for this operation
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        int result;

        if (LogConfig.DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "update: uri=" + uri + ", match is " + match);
        }

        // TODO : Add the "case" lines depending on your matcher
        switch (match) {
            case SKELETON_ID:
                // case PROGRAM_ID:
                final String id = uri.getPathSegments().get(1);
                result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection), selectionArgs);
                break;
            case SKELETON:
                // case PROGRAM:
                result = db.update(TABLE_NAMES[table], values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public boolean onCreate() {
        return true;
    }
}
