/*
 * Copyright (C) 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foxykeep.dataproxy.provider;

import android.database.sqlite.SQLiteDatabase;

/**
 * DatabaseContent is the superclass of the various classes of content stored by
 * the ContentProvider of your project. It is intended to include :
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
