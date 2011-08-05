/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.util;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

import com.foxykeep.datadroidpoc.config.SharedPrefsConfig;

public class UserManager {

    public static String getUserId(final Context context) {

        final SharedPreferences sharedPrefs = context.getSharedPreferences(SharedPrefsConfig.SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE);
        String userId = sharedPrefs.getString(SharedPrefsConfig.SHARED_PREFS_USER_ID, null);

        if (userId == null) {
            userId = UUID.randomUUID().toString();
            sharedPrefs.edit().putString(SharedPrefsConfig.SHARED_PREFS_USER_ID, userId).commit();
        }

        return userId;
    }
}
