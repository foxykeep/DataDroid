/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroid.provider.util;

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
        return "create index " + tableName + '_' + columnName + " on " + tableName + " (" + columnName
                + ");";
    }

	/**
	 * Creates the string used by the database to create an trigger that prevents insert
	 * into the childTable if the foreignKey matching the primaryKey in the parentTable
	 * does not exist. This prevents the creation orphaned rows in dependent tables.
     * This string can be used in the function
     * {@link SQLiteDatabase#execSQL(String)}
     * 
	 * @param parentTable The parent table
	 * @param primaryKey The primary key in the parent table
	 * @param childTable The dependent table
	 * @param foreignKey The foreign key in the dependent table
	 * @return The prevent insert trigger string
	 */
	public static String getPreventInsertString(final String parentTable, final String primaryKey, 
			final String childTable, final String foreignKey) {
		String triggerName = "pi_" + parentTable + "_" + primaryKey + "_" + childTable + "_"
				+ foreignKey;
		return "create trigger " + triggerName + " before insert on [" + childTable
				+ "] for each row begin select raise(rollback, 'insert on table \"" + childTable
				+ "\" violates foreign key constraint \"" + triggerName + "\"') where new." + foreignKey
				+ " is not null and (select " +  primaryKey + " from " + parentTable + " where " + primaryKey
				+ " = new." + foreignKey + ") is null; end;";
	}

	/**
	 * Creates the string used by the database to create an trigger that prevents update
	 * into the childTable if the foreignKey matching the primaryKey in the parentTable
	 * does not exist. 
     * This string can be used in the function
     * {@link SQLiteDatabase#execSQL(String)}
     * 
	 * @param parentTable The parent table
	 * @param primaryKey The primary key in the parent table
	 * @param childTable The dependent table
	 * @param foreignKey The foreign key in the dependent table
	 * @return The prevent update trigger string
	 */
	public static String getPreventUpdateString(final String parentTable, final String primaryKey, 
			final String childTable, final String foreignKey) {
		String triggerName = "pu_" + parentTable + "_" + primaryKey + "_" + childTable + "_"
				+ foreignKey;
		return "create trigger " + triggerName + " before update on [" + childTable
				+ "] for each row begin select raise(rollback, 'update on table \"" + childTable
				+ "\" violates foreign key constraint \"" + triggerName + "\"') where new." + foreignKey
				+ " is not null and (select " +  primaryKey + " from " + parentTable + " where " + primaryKey
				+ " = new." + foreignKey + ") is null; end;";
	}

	/**
	 * Creates the string used by the database to create an trigger that prevents delete
	 * of parentTable rows if the childTable has rows in which the foreignKey matches the
	 * primaryKey. This prevents rows in dependent tabled from being orphaned. DO NOT USE
	 * WITH getDeleteCascadeString().
     * This string can be used in the function
     * {@link SQLiteDatabase#execSQL(String)}
     * 
	 * @param parentTable The parent table
	 * @param primaryKey The primary key in the parent table
	 * @param childTable The dependent table
	 * @param foreignKey The foreign key in the dependent table
	 * @return The prevent update trigger string
	 */
	public static String getPreventDeleteString(final String parentTable, final String primaryKey, 
			final String childTable, final String foreignKey) {
		String triggerName = "pd_" + parentTable + "_" + primaryKey + "_" + childTable + "_"
				+ foreignKey;
		return "create trigger " + triggerName + " before delete on [" + childTable
				+ "] for each row begin select raise(rollback, 'delete on table \"" + childTable
				+ "\" violates foreign key constraint \"" + triggerName + "\"') where new." + foreignKey
				+ " is not null and (select " +  foreignKey + " from " + childTable + " where " + foreignKey
				+ " = old." + primaryKey + ") is not null; end;";
	}
	
	/**
	 * Creates the string used by the database to create an trigger that deletes rows in
	 * the childTable when the foreignKey matching the primaryKey in the parentTable is 
	 * deleted. This cleans up orphaned rows in dependent tables. DO NOT USE WITH
	 * getDeleteCascadeString().
     * This string can be used in the function
     * {@link SQLiteDatabase#execSQL(String)}
     * 
	 * @param parentTable The parent table
	 * @param primaryKey The primary key in the parent table
	 * @param childTable The dependent table
	 * @param foreignKey The foreign key in the dependent table
	 * @return The prevent update trigger string
	 */
	public static String getDeleteCascadeString(final String parentTable, final String primaryKey, 
			final String childTable, final String foreignKey) {
		return "create trigger dc_" + parentTable + "_" + primaryKey + "_" + childTable + "_"
				+ foreignKey + " before delete on [" + parentTable + "] for each row begin delete from " 
				+ childTable + " where " + childTable + "." + foreignKey + " = old." + primaryKey + "; end;";
    }
}
