/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxy.provider.util;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtil {

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
