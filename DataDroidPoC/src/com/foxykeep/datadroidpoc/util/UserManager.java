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
