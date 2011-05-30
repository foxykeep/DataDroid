/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxy.provider;

import android.database.sqlite.SQLiteDatabase;

/**
 * {@link DatabaseContent} is the superclass of the various classes of content
 * stored by the ContentProvider of your project. It is intended to include :
 * <ol>
 * <li>column definitions for use with the Provider</li>
 * <li>convenience methods for saving and retrieving content from the Provider.</li>
 * </ol>
 * Conventions used in naming columns:
 * <ul>
 * <li>RECORD_ID is the primary key for all records</li>
 * <li>(name)_KEY always refers to a foreign key</li>
 * <li>(name)_ID always refers to a unique identifier (whether on client,
 * server, etc.)</li>
 * </ul>
 * 
 * @author Foxykeep
 */
public abstract class DatabaseContent {
    public static final String RECORD_ID = "_id";

    /**
     * This projection can be used with any of the DatabaseContent subclasses,
     * when all you need is a list of id's. Use {@link #ID_PROJECTION_COLUMN} to
     * access the row data.
     */
    public static final String[] ID_PROJECTION = new String[] {
        RECORD_ID
    };
    public static final int ID_PROJECTION_COLUMN = 0;

    /**
     * Creates the string used by the database to create an index in a table.
     * This string can be used in the function
     * {@link SQLiteDatabase#execSQL(String)}
     * 
     * @param tableName The name of the table
     * @param columnName The name of the column to index
     * @return The index string
     */
    public static String getCreateIndexString(final String tableName, final String columnName) {
        return "create index " + tableName.toLowerCase() + '_' + columnName + " on " + tableName + " (" + columnName
                + ");";
    }
}
