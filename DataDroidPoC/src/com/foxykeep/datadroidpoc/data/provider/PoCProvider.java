/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.foxykeep.datadroidpoc.config.LogConfig;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.PersonDao;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.PhoneDao;

import java.util.ArrayList;

public class PoCProvider extends ContentProvider {

    private static final String LOG_TAG = PoCProvider.class.getSimpleName();

    protected static final String DATABASE_NAME = "DataManagerPoC.db";

    // Any changes to the database format *must* include update-in-place code.
    // Original version: 1
    public static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.foxykeep.datadroidpoc.data.provider.PoCProvider";

    public static final Uri INTEGRITY_CHECK_URI = Uri.parse("content://" + AUTHORITY
            + "/integrityCheck");

    private static final int PERSON_BASE = 0;
    private static final int PERSON = PERSON_BASE;
    private static final int PERSON_ID = PERSON_BASE + 1;

    private static final int PHONE_BASE = 0x1000;
    private static final int PHONE = PHONE_BASE;
    private static final int PHONE_ID = PHONE_BASE + 1;

    private static final int BASE_SHIFT = 12; // DO NOT TOUCH ! 12 bits to the
    // base type: 0,
    // 0x1000, 0x2000, etc.

    private static final String[] TABLE_NAMES = {
            PersonDao.TABLE_NAME, PhoneDao.TABLE_NAME
    };

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        final UriMatcher matcher = sURIMatcher;

        // All persons.
        matcher.addURI(AUTHORITY, PersonDao.TABLE_NAME, PERSON);
        // A specific person.
        matcher.addURI(AUTHORITY, PersonDao.TABLE_NAME + "/#", PERSON_ID);

        // All phones.
        matcher.addURI(AUTHORITY, PhoneDao.TABLE_NAME, PHONE);
        // A specific phone.
        matcher.addURI(AUTHORITY, PhoneDao.TABLE_NAME + "/#", PHONE_ID);
    }

    private SQLiteDatabase mDatabase;

    public synchronized SQLiteDatabase getDatabase(final Context context) {
        // Always return the cached database, if we've got one.
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
        final DatabaseHelper helper = new PoCProvider().new DatabaseHelper(context, DATABASE_NAME);
        return helper.getReadableDatabase();
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(final Context context, final String name) {
            super(context, name, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            Log.d(LOG_TAG, "Creating database");

            // Creates all tables here; each class has its own method.
            if (LogConfig.DDP_DEBUG_LOGS_ENABLED) {
                Log.d(LOG_TAG, "PoCProvider | createPersonTable start");
            }
            PersonDao.createTable(db);
            if (LogConfig.DDP_DEBUG_LOGS_ENABLED) {
                Log.d(LOG_TAG, "PoCProvider | createPersonTable end");
                Log.d(LOG_TAG, "PoCProvider | createPhoneTable start");
            }
            PhoneDao.createTable(db);
            if (LogConfig.DDP_DEBUG_LOGS_ENABLED) {
                Log.d(LOG_TAG, "PoCProvider | createPhoneTable end");
            }
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        }

        @Override
        public void onOpen(final SQLiteDatabase db) {
        }
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {

        final int match = sURIMatcher.match(uri);
        final Context context = getContext();

        // Pick the correct database for this operation.
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        String id = "0";

        if (LogConfig.DDP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "delete: uri=" + uri + ", match is " + match);
        }

        int result = -1;

        switch (match) {
            case PERSON_ID:
            case PHONE_ID:
                id = uri.getPathSegments().get(1);
                result = db.delete(TABLE_NAMES[table], whereWithId(id, selection), selectionArgs);
                break;
            case PERSON:
            case PHONE:
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
        switch (match) {
            case PERSON_ID:
                return PersonDao.TYPE_ELEM_TYPE;
            case PERSON:
                return PersonDao.TYPE_DIR_TYPE;
            case PHONE_ID:
                return PhoneDao.TYPE_ELEM_TYPE;
            case PHONE:
                return PhoneDao.TYPE_DIR_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {

        final int match = sURIMatcher.match(uri);
        final Context context = getContext();

        // Pick the correct database for this operation.
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        long id;

        if (LogConfig.DDP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "insert: uri=" + uri + ", match is " + match);
        }

        Uri resultUri = null;

        switch (match) {
            case PERSON:
            case PHONE:
                id = db.insert(TABLE_NAMES[table], "foo", values);
                resultUri = id == -1 ? null : ContentUris.withAppendedId(uri, id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Notify with the base uri, not the new uri (nobody is watching a new
        // record).
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public ContentProviderResult[] applyBatch(final ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = getDatabase(getContext());
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection,
            final String[] selectionArgs, final String sortOrder) {

        Cursor c = null;
        final Uri notificationUri = PoCContent.CONTENT_URI;
        final int match = sURIMatcher.match(uri);
        final Context context = getContext();
        // Pick the correct database for this operation
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        String id;

        if (LogConfig.DDP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "query: uri=" + uri + ", match is " + match);
        }

        switch (match) {
            case PERSON_ID:
            case PHONE_ID:
                id = uri.getPathSegments().get(1);
                c = db.query(TABLE_NAMES[table], projection, whereWithId(id, selection),
                        selectionArgs, null, null, sortOrder);
                break;
            case PERSON:
            case PHONE:
                c = db.query(TABLE_NAMES[table], projection, selection, selectionArgs, null, null,
                        sortOrder);
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
        sb.append(BaseColumns._ID);
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
    public int update(final Uri uri, final ContentValues values, final String selection,
            final String[] selectionArgs) {

        final int match = sURIMatcher.match(uri);
        final Context context = getContext();
        // Pick the correct database for this operation.
        final SQLiteDatabase db = getDatabase(context);
        final int table = match >> BASE_SHIFT;
        int result;

        if (LogConfig.DDP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "update: uri=" + uri + ", match is " + match);
        }

        switch (match) {
            case PERSON_ID:
            case PHONE_ID:
                final String id = uri.getPathSegments().get(1);
                result = db.update(TABLE_NAMES[table], values, whereWithId(id, selection),
                        selectionArgs);
                break;
            case PERSON:
            case PHONE:
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
